package org.jeecgframework.web.system.sms.util.task;

import com.jeecg.common.CalendarUtil;
import com.jeecg.common.DateUtil;
import org.apache.log4j.Logger;
import org.jeecgframework.core.util.DynamicDBUtil;
import org.jeecgframework.web.system.pojo.oms.WarehouseCountEntity;
import org.jeecgframework.web.system.sms.service.WarehouseCountServiceI;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by jiang.zheng on 2018/1/3.
 */
@Component("WarehouseCountTask")
public class WarehouseCountTask implements Job {

    private static final Logger logger = Logger.getLogger(WarehouseCountTask.class);
    private static final String dbKey = "OMS_Mysql";

    @Autowired
    private WarehouseCountServiceI warehouseCountService;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        run();
    }

    private void run() {
        logger.info("-------- TestTas k run start ----------");
        Calendar calendar = Calendar.getInstance();
        // 统计上一个小时前的数据
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        //检查是否已统计当前数据
        if (!checkIfExist(calendar.getTime())) {
            warehouseCount(calendar.getTime());
        } else {
            logger.info(CalendarUtil.hoursStartTime(calendar.getTime()) + " 库存数据已统计，跳过当前时段");
        }
        logger.info("-------- TestTask run end ----------");
    }

    /**
     * 检查数据是否存在
     * @param date
     * @return
     */
    private boolean checkIfExist(Date date) {
        String startDateTime = CalendarUtil.hoursStartTime(date);
        Long count = warehouseCountService.getCountForJdbcParam("SELECT COUNT(1) FROM oms_warehouse_count_day WHERE date_hours=?", new Object[]{startDateTime});
        if (count>0) return true;
        return false;
    }

    /**
     * 库存量统计
     */
    private void warehouseCount(Date date) {
        String startDateTime = CalendarUtil.hoursStartTime(date);
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT\n" +
                "\tt1.warehouseId\n" +
                "\t,(CASE WHEN t1.warehouseId='0' THEN '总仓' ELSE t2.`name` END) warehouseName\n" +
                " ,(CASE WHEN t1.warehouseId='0' THEN '总仓'\n" +
                "\t\t\t\tWHEN t2.type='WareHouseType@join' THEN '代理商'\n" +
                "\t\t\t\tWHEN t2.`name` LIKE '%差异仓%' THEN '差异仓'\n" +
                "\t\t\t\tWHEN t2.parentId IS NULL THEN t2.`name` \n" +
                "\t\t\t\tWHEN t2.classes IN ('WareHouseClasses@Point', 'WareHouseClasses@Cabinet') THEN (SELECT `name` FROM warehouse WHERE id=t2.parentId AND isDelete='0')\n" +
                "\t\t\t\tELSE t2.`name` END) belonging\n" +
                "\t,MID(t1.deviceType,12) typeName \n" +
                "\t,t1.deviceStatus");
        sb.append("\t,'" + startDateTime + "' dateHours\n");
        sb.append("\t,COUNT(1) deviceCount\n" +
                "FROM\n" +
                "\tdevice t1 LEFT JOIN warehouse t2 ON t1.warehouseId=t2.id AND t2.isDelete='0'\n" +
                "WHERE \n" +
                "\t t1.deviceStatus IN (\"DeviceState@in\",\"DeviceState@hire\", \"DeviceState@borrow\", \"DeviceState@on\") \n" +
                "\tAND t1.isDelete = '0' AND t1.dataStatus = 'Common@valid'\n" +
                "GROUP BY t1.warehouseId, t1.deviceType, t1.deviceStatus");
        List<Map<String, Object>> list = DynamicDBUtil.findList(dbKey, sb.toString());
//        logger.info("warehouseCount: " + list);
        warehouseCountService.batchSave(mapToEntities(list));
    }

    /**
     * map to entity
     * @param list
     * @return
     */
    private List<WarehouseCountEntity> mapToEntities(List<Map<String, Object>> list) {
        List<WarehouseCountEntity> entities = new ArrayList<>();
        WarehouseCountEntity entity;
        for (Map<String, Object> m : list) {
            entity = new WarehouseCountEntity();
            entity.setWarehouseId((Integer) m.get("warehouseId"));
            entity.setWarehouseName((String) m.get("warehouseName"));
            entity.setBelonging((String) m.get("belonging"));
            entity.setTypeName((String) m.get("typeName"));
            entity.setDeviceStatus((String) m.get("deviceStatus"));
            entity.setDateHours(DateUtil.defaultDate((String) m.get("dateHours")));
            entity.setDeviceCount((Long) m.get("deviceCount"));
            entities.add(entity);
        }
        return entities;
    }
}
