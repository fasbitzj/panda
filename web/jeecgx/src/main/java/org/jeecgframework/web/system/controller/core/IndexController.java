package org.jeecgframework.web.system.controller.core;

import org.apache.log4j.Logger;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.core.enums.SysThemesEnum;
import org.jeecgframework.core.util.*;
import org.jeecgframework.web.system.manager.ClientManager;
import org.jeecgframework.web.system.pojo.base.Client;
import org.jeecgframework.web.system.pojo.base.TSDepart;
import org.jeecgframework.web.system.pojo.base.TSFunction;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.jeecgframework.web.system.service.MutiLangServiceI;
import org.jeecgframework.web.system.service.SystemService;
import org.jeecgframework.web.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by jiang.zheng on 2018/2/7.
 */
@Controller
@RequestMapping(value = "indexController")
public class IndexController {

    private static final Logger log = Logger.getLogger(IndexController.class);

    @Autowired
    private SystemService systemService;

    @Autowired
    private UserService userService;

    @Autowired
    private MutiLangServiceI mutiLangService;

    @RequestMapping(params = "index")
    public String index(ModelMap modelMap,HttpServletRequest request,HttpServletResponse response) {
        SysThemesEnum sysTheme = SysThemesUtil.getSysTheme(request);
        TSUser user = userService.get(TSUser.class, "8a8ab0b246dc81120146dc8181a10054");
        Map<String, Object> userOrgMap = systemService.findOneForJdbc("select org_id as orgId from t_s_user_org where user_id=? LIMIT 1", user.getId());
        saveLoginSuccessInfo(request, user, (String) userOrgMap.get("orgId"));
        request.setAttribute("menuMap", getFunctionMap(user));
        return sysTheme.getIndexPath();
    }

    /**
     * 保存用户登录的信息，并将当前登录用户的组织机构赋值到用户实体中；
     * @param req request
     * @param user 当前登录用户
     * @param orgId 组织主键
     */
    private void saveLoginSuccessInfo(HttpServletRequest req, TSUser user, String orgId) {
        String message = null;
        TSDepart currentDepart = systemService.get(TSDepart.class, orgId);
        user.setCurrentDepart(currentDepart);

        HttpSession session = ContextHolderUtils.getSession();

        user.setDepartid(orgId);

        session.setAttribute(ResourceUtil.LOCAL_CLINET_USER, user);
        message = mutiLangService.getLang("common.user") + ": " + user.getUserName() + "["+ currentDepart.getDepartname() + "]" + mutiLangService.getLang("common.login.success");

        String browserType = "";
        Cookie[] cookies = req.getCookies();
        for (int i = 0; i < cookies.length; i++) {
            Cookie cookie = cookies[i];
            if("BROWSER_TYPE".equals(cookie.getName())){
                browserType = cookie.getValue();
            }
        }
        session.setAttribute("brower_type", browserType);

        //当前session为空 或者 当前session的用户信息与刚输入的用户信息一致时，则更新Client信息
        Client clientOld = ClientManager.getInstance().getClient(session.getId());
        if(clientOld == null || clientOld.getUser() ==null ||user.getUserName().equals(clientOld.getUser().getUserName())){
            Client client = new Client();
            client.setIp(IpUtil.getIpAddr(req));
            client.setLogindatetime(new Date());
            client.setUser(user);
            ClientManager.getInstance().addClinet(session.getId(), client);
        } else {//如果不一致，则注销session并通过session=req.getSession(true)初始化session
            ClientManager.getInstance().removeClinet(session.getId());
            session.invalidate();
            session = req.getSession(true);//session初始化
            session.setAttribute(ResourceUtil.LOCAL_CLINET_USER, user);
            session.setAttribute("randCode",req.getParameter("randCode"));//保存验证码
//            checkuser(user,req);
        }



        // 添加登陆日志
        systemService.addLog(message, Globals.Log_Type_LOGIN, Globals.Log_Leavel_INFO);
    }

    /**
     * 获取权限的map
     *
     * @param user
     * @return
     */
    private Map<Integer, List<TSFunction>> getFunctionMap(TSUser user) {
        HttpSession session = ContextHolderUtils.getSession();
        Client client = ClientManager.getInstance().getClient(session.getId());
        if (client.getFunctionMap() == null || client.getFunctionMap().size() == 0) {
            Map<Integer, List<TSFunction>> functionMap = new HashMap<Integer, List<TSFunction>>();
            Map<String, TSFunction> loginActionlist = getUserFunction(user);
            if (loginActionlist.size() > 0) {
                Collection<TSFunction> allFunctions = loginActionlist.values();
                for (TSFunction function : allFunctions) {
                    if(function.getFunctionType().intValue()== Globals.Function_TYPE_FROM.intValue()){
                        //如果为表单或者弹出 不显示在系统菜单里面
                        continue;
                    }
                    if (!functionMap.containsKey(function.getFunctionLevel() + 0)) {
                        functionMap.put(function.getFunctionLevel() + 0,
                                new ArrayList<TSFunction>());
                    }
                    functionMap.get(function.getFunctionLevel() + 0).add(function);
                }
                // 菜单栏排序
                Collection<List<TSFunction>> c = functionMap.values();
                for (List<TSFunction> list : c) {
                    Collections.sort(list, new NumberComparator());
                }
            }
            client.setFunctionMap(functionMap);

            //清空变量，降低内存使用
            loginActionlist.clear();

            return functionMap;
        }else{
            return client.getFunctionMap();
        }
    }

    /**
     * 获取用户菜单列表
     *
     * @param user
     * @return
     */
    private Map<String, TSFunction> getUserFunction(TSUser user) {
        HttpSession session = ContextHolderUtils.getSession();
        Client client = ClientManager.getInstance().getClient(session.getId());

        if (client.getFunctions() == null || client.getFunctions().size() == 0) {

            Map<String, TSFunction> loginActionlist = new HashMap<String, TSFunction>();

			 /*String hql="from TSFunction t where t.id in  (select d.TSFunction.id from TSRoleFunction d where d.TSRole.id in(select t.TSRole.id from TSRoleUser t where t.TSUser.id ='"+
	           user.getId()+"' ))";
	           String hql2="from TSFunction t where t.id in  ( select b.tsRole.id from TSRoleOrg b where b.tsDepart.id in(select a.tsDepart.id from TSUserOrg a where a.tsUser.id='"+
	           user.getId()+"'))";
	           List<TSFunction> list = systemService.findHql(hql);
	           log.info("role functions:  "+list.size());
	           for(TSFunction function:list){
	              loginActionlist.put(function.getId(),function);
	           }
	           List<TSFunction> list2 = systemService.findHql(hql2);
	           log.info("org functions: "+list2.size());
	           for(TSFunction function:list2){
	              loginActionlist.put(function.getId(),function);
	           }*/

            StringBuilder hqlsb1=new StringBuilder("select distinct f from TSFunction f,TSRoleFunction rf,TSRoleUser ru  ").append("where ru.TSRole.id=rf.TSRole.id and rf.TSFunction.id=f.id and ru.TSUser.id=? ");

            StringBuilder hqlsb2=new StringBuilder("select distinct c from TSFunction c,TSRoleFunction rf,TSRoleOrg b,TSUserOrg a ")
                    .append("where a.tsDepart.id=b.tsDepart.id and b.tsRole.id=rf.TSRole.id and rf.TSFunction.id=c.id and a.tsUser.id=?");
            //TODO hql执行效率慢 为耗时最多地方

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            log.info("================================开始时间:"+sdf.format(new Date())+"==============================");
            long start = System.currentTimeMillis();
            List<TSFunction> list1 = systemService.findHql(hqlsb1.toString(),user.getId());
            List<TSFunction> list2 = systemService.findHql(hqlsb2.toString(),user.getId());
            long end = System.currentTimeMillis();
            log.info("================================结束时间:"+sdf.format(new Date())+"==============================");
            log.info("================================耗时:"+(end-start)+"ms==============================");
            for(TSFunction function:list1){
                loginActionlist.put(function.getId(),function);
            }
            for(TSFunction function:list2){
                loginActionlist.put(function.getId(),function);
            }
            client.setFunctions(loginActionlist);

            //清空变量，降低内存使用
            list2.clear();
            list1.clear();

        }
        return client.getFunctions();
    }
}
