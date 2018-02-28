package org.jeecgframework.web.system.sms.util.task;

import com.jeecg.common.CalendarUtil;
import org.apache.log4j.Logger;
import org.jeecgframework.core.util.DynamicDBUtil;
import org.jeecgframework.web.system.pojo.oms.FutureDeviceCountDayEntity;
import org.jeecgframework.web.system.sms.service.DeviceCountServiceI;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by jiang.zheng on 2018/1/5.
 */
public class FutureDeviceCountTask implements Job {

    private static final Logger logger = Logger.getLogger(FutureDeviceCountTask.class);

    @Autowired
    private DeviceCountServiceI deviceCountService;

    private static final String dbKey = "OMS_Mysql";

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        run();
    }

    private void run() {
        logger.info("------- FutureDeviceCountTask start --------");
        futureDeviceCount();
        logger.info("------- FutureDeviceCountTask end --------");
    }

    private void futureDeviceCount() {
        try {
            Calendar calendar = Calendar.getInstance();
            // 统计上一天前的数据
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            String startDateTime = CalendarUtil.daysStartTime(calendar.getTime());
            calendar.add(Calendar.MONTH, 1);
            String endDateTime = CalendarUtil.daysEndTime(calendar.getTime());
            // 1、删除旧数据
            truncate();
            // 2、统计并入库
            List<Map<String, Object>> list = futureDeviceCount(startDateTime, endDateTime);
            deviceCountService.batchSave(mapToEntities(list));
        } catch (Exception e) {
            logger.error("预计发货归还统计数据异常", e);
        }
    }

    /**
     *
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    private List<Map<String, Object>> futureDeviceCount(String startDateTime, String endDateTime) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT \n" +
                "\tt1.warehouseId\n" +
                "\t,t3.`name` warehouseName\n" +
                "\t,(CASE WHEN t3.id='0' THEN '总仓'\n" +
                "\t\t\t\tWHEN t3.type='WareHouseType@join' THEN '代理商'\n" +
                "\t\t\t\tWHEN t3.`name` LIKE '%差异仓%' THEN '差异仓'\n" +
                "\t\t\t\tWHEN t3.parentId IS NULL THEN t3.`name` \n" +
                "\t\t\t\tWHEN t3.classes IN ('WareHouseClasses@Point', 'WareHouseClasses@Cabinet') THEN (SELECT `name` FROM warehouse WHERE id=t3.parentId AND isDelete='0')\n" +
                "\t\t\t\tELSE t3.`name` END) belonging\n" +
                "\t,t1.dateDays,t1.fDeliverCount\n" +
                "\t,IFNULL(t2.fReturnCount, 0) fReturnCount\n" +
                "FROM\n" +
                "(SELECT\n" +
                "\tareaId warehouseId,startDate dateDays,COUNT(1) fDeliverCount\n" +
                "FROM\n" +
                "\torder_basic\n" +
                "WHERE\n" +
                "\t`status` = 'Common@valid' AND isDelete = '0' \n" +
                "AND orderStatus NOT IN ('OrderStatus@camcel', 'OrderStatus@unsub', 'OrderStatus@invalid')\n");
        sb.append("AND startDate>='" + startDateTime + "'\n");
//        sb.append("AND startDate<='" + endDateTime + "'\n");
        sb.append("GROUP BY startDate, areaId) t1\n" +
                "LEFT JOIN \n" +
                "(SELECT\n" +
                "\tareaId warehouseId,DATE_ADD(endDate,INTERVAL 1 SECOND) dateDays,COUNT(1) fReturnCount\n" +
                "FROM\n" +
                "\torder_basic\n" +
                "WHERE\n" +
                "\t`status` = 'Common@valid' AND isDelete = '0' \n" +
                "AND orderStatus NOT IN ('OrderStatus@camcel', 'OrderStatus@unsub', 'OrderStatus@invalid')\n");
        sb.append("AND endDate>='" + startDateTime + "'\n");
//        sb.append("AND endDate<='" + endDateTime + "'\n");
        sb.append("GROUP BY endDate, areaId) t2 ON t1.warehouseId=t2.warehouseId AND t2.dateDays=t1.dateDays\n" +
                "LEFT JOIN warehouse t3 ON t1.warehouseId=t3.id AND t3.isDelete='0'\n" +
                "UNION\n" +
                "SELECT \n" +
                "\tt2.warehouseId\n" +
                "\t,t3.`name` warehouseName\n" +
                "\t,(CASE WHEN t3.id='0' THEN '总仓'\n" +
                "\t\t\t\tWHEN t3.type='WareHouseType@join' THEN '代理商'\n" +
                "\t\t\t\tWHEN t3.`name` LIKE '%差异仓%' THEN '差异仓'\n" +
                "\t\t\t\tWHEN t3.parentId IS NULL THEN t3.`name` \n" +
                "\t\t\t\tWHEN t3.classes IN ('WareHouseClasses@Point', 'WareHouseClasses@Cabinet') THEN (SELECT `name` FROM warehouse WHERE id=t3.parentId AND isDelete='0')\n" +
                "\t\t\t\tELSE t3.`name` END) belonging\n" +
                "\t,t2.dateDays\n" +
                "\t,IFNULL(t1.fDeliverCount, 0) fDeliverCount\n" +
                "\t,t2.fReturnCount\n" +
                "FROM\n" +
                "(SELECT\n" +
                "\tareaId warehouseId,startDate dateDays,COUNT(1) fDeliverCount\n" +
                "FROM\n" +
                "\torder_basic\n" +
                "WHERE\n" +
                "\t`status` = 'Common@valid' AND isDelete = '0' \n" +
                "AND orderStatus NOT IN ('OrderStatus@camcel', 'OrderStatus@unsub', 'OrderStatus@invalid')\n");
        sb.append("AND startDate>='" + startDateTime + "'\n");
//        sb.append("AND startDate<='" + endDateTime + "'\n");
        sb.append("GROUP BY startDate, areaId) t1\n" +
                "RIGHT JOIN\n" +
                "(SELECT\n" +
                "\tareaId warehouseId,DATE_ADD(endDate,INTERVAL 1 SECOND) dateDays,COUNT(1) fReturnCount\n" +
                "FROM\n" +
                "\torder_basic\n" +
                "WHERE\n" +
                "\t`status` = 'Common@valid' AND isDelete = '0' \n" +
                "AND orderStatus NOT IN ('OrderStatus@camcel', 'OrderStatus@unsub', 'OrderStatus@invalid')\n");
        sb.append("AND endDate>='" + startDateTime + "'\n");
//        sb.append("AND endDate<='" + endDateTime + "'\n");
        sb.append("GROUP BY endDate, areaId) t2 ON t1.warehouseId=t2.warehouseId AND t2.dateDays=t1.dateDays\n" +
                "LEFT JOIN warehouse t3 ON t2.warehouseId=t3.id AND t3.isDelete='0'");
        return DynamicDBUtil.findList(dbKey, sb.toString());
    }

    /**
     *
     * @param list
     * @return
     */
    private List<FutureDeviceCountDayEntity> mapToEntities(List<Map<String, Object>> list) {
        List<FutureDeviceCountDayEntity> entities = new ArrayList<>();
        FutureDeviceCountDayEntity entity;
        for (Map<String, Object> m : list) {
            entity = new FutureDeviceCountDayEntity();
            entity.setWarehouseId((Integer) m.get("warehouseId"));
            entity.setWarehouseName((String) m.get("warehouseName"));
            entity.setBelonging((String) m.get("belonging"));
//            entity.setTakeType((String) m.get("takeType"));
            entity.setDateDays(CalendarUtil.daysStart((Date) m.get("dateDays")));
            entity.setDeliverCount((Long) m.get("fDeliverCount"));
            entity.setReturnCount((Long) m.get("fReturnCount"));
            entities.add(entity);
        }
        return entities;
    }

    private void truncate() {
        deviceCountService.executeSql("TRUNCATE oms_future_device_count_day");
    }
}
