package org.jeecgframework.web.system.sms.util.task;

import com.jeecg.common.CalendarUtil;
import com.jeecg.common.DateUtil;
import org.apache.log4j.Logger;
import org.jeecgframework.core.util.DynamicDBUtil;
import org.jeecgframework.web.system.pojo.oms.DeviceCountDayEntity;
import org.jeecgframework.web.system.pojo.oms.DeviceCountHourEntity;
import org.jeecgframework.web.system.sms.service.DeviceCountServiceI;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by jiang.zheng on 2017/12/29.
 */
@Component("DeviceCountTask")
public class DeviceCountTask implements Job {

    private static final Logger logger = Logger.getLogger(DeviceCountTask.class);
    @Autowired
    private DeviceCountServiceI deviceCountService;

    private static final String dbKey = "OMS_Mysql";

    // 每次检查前三个小时到现在的数据，缺失则统计
    private static final int BEFORE_HOURS_COUNT = 3;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        run();
    }

    private void run() {
        logger.info("-------- DeviceCountTask run start ----------");
        deviceCountHour();
        logger.info("-------- DeviceCountTask run end ----------");
    }

    /**
     * 按小时统计
     */
    private void deviceCountHour() {
        try {
            Calendar calendar = Calendar.getInstance();
            // 统计上一个小时前的数据
            calendar.add(Calendar.HOUR_OF_DAY, -1);
            String startDateTime;
            String endDateTime;
            int i = 0;
            do {
                //检查是否已统计当前数据
                startDateTime = CalendarUtil.hoursStartTime(calendar.getTime());
                endDateTime = CalendarUtil.hoursEndTime(calendar.getTime());
                if (!checkIfExistForHours(startDateTime)) {
                    List<Map<String, Object>> list = deliverReturnCount(startDateTime, endDateTime);
                    deviceCountService.batchSave(mapToHoursEntities(list));
                } else {
                    logger.info(startDateTime + " 设备数据已统计，跳过当前时段");
                }
                calendar.add(Calendar.HOUR_OF_DAY, -1);
                i++;
            } while (i<=BEFORE_HOURS_COUNT);
        } catch (Exception e) {
            logger.error("按小时统计设备数异常", e);
        }
    }

    /**
     * 检查数据是否存在
     * @param startDateTime
     * @return
     */
    private boolean checkIfExistForHours(String startDateTime) {
        Long count = deviceCountService.getCountForJdbcParam("SELECT COUNT(1) FROM oms_device_count_hour WHERE date_hours=?", new Object[]{startDateTime});
        if (count>0) return true;
        return false;
    }

    /**
     *
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    private List<Map<String, Object>> deliverReturnCount(String startDateTime, String endDateTime) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT \n" +
                "\t t1.warehouseId warehouseId, t3.`name` warehouseName\n" +
                "\t,(CASE WHEN t3.id='0' THEN '总仓'\n" +
                "\t\t\t\tWHEN t3.type='WareHouseType@join' THEN '代理商'\n" +
                "\t\t\t\tWHEN t3.`name` LIKE '%差异仓%' THEN '差异仓'\n" +
                "\t\t\t\tWHEN t3.parentId IS NULL THEN t3.`name` \n" +
                "\t\t\t\tWHEN t3.classes IN ('WareHouseClasses@Point', 'WareHouseClasses@Cabinet') THEN (SELECT `name` FROM warehouse WHERE id=t3.parentId AND isDelete='0')\n" +
                "\t\t\t\tELSE t3.`name` END) belonging\n" +
                "\t,(CASE WHEN t1.takeType='TakeType@cabinet' THEN '机柜' ELSE \n" +
                "\t\t\t(CASE WHEN t1.takeType='TakeType@express' THEN '快递' ELSE \n" +
                "\t\t\t\t(CASE WHEN t1.takeType='TakeType@point' THEN '网点' ELSE \n" +
                "\t\t\t\t\t(CASE WHEN t1.takeType='TakeType@inventory' THEN '代理商库存' ELSE t1.takeType END) \n" +
                "\t\t\t\tEND) \n" +
                "\t\t\tEND) \n" +
                "\t\tEND) takeType\n" +
                "\t, t1.deliverCount deliverCount, IFNULL(t2.returnCount,0) returnCount\n");
        sb.append("\t, t1.date_hours dateHours \n");
        sb.append("\tFROM\n" +
                "(SELECT \n" +
                "\ta2.areaId warehouseId, a2.areaName warehouseName, a1.takeType, COUNT(1) deliverCount, DATE_FORMAT(a1.deliverDate, '%Y-%m-%d %H:00:00') date_hours\n" +
                "FROM order_deliver a1, order_basic a2\n" +
                "WHERE a1.orderId=a2.id AND a2.isDelete='0' AND a2.`status`='Common@valid' \n" +
                "AND a1.isDelete='0' AND a1.takeType!='TakeType@inventory' AND a1.deliverDate IS NOT NULL \n");
        sb.append("\tAND a1.deliverDate>= '" + startDateTime + "' AND a1.deliverDate<= '" + endDateTime + "'\n");
        sb.append("\tGROUP BY a2.areaId, a1.takeType, date_hours) t1\n");
        sb.append(" LEFT JOIN\n");
        sb.append("\t(SELECT \n" +
                "\ta.returnWarehouseId warehouseId, b.takeType, COUNT(1) returnCount, DATE_FORMAT(a.returnTime, '%Y-%m-%d %H:00:00') date_hours \n" +
                "FROM order_return a, order_deliver b\n" +
                "WHERE a.orderId=b.orderId AND a.isDelete='0' AND b.isDelete='0' AND b.takeType!='TakeType@inventory'\n");
        sb.append("\tAND a.returnTime>='" + startDateTime + "' AND a.returnTime<='" + endDateTime + "'\n");
        sb.append("\tGROUP BY a.returnWarehouseId, b.takeType, date_hours) t2 ON t1.warehouseId=t2.warehouseId AND t1.takeType=t2.takeType AND t1.date_hours=t2.date_hours\n" +
                "\tLEFT JOIN warehouse t3 ON t1.warehouseId=t3.id AND t3.isDelete='0'\n");
        sb.append("UNION \n");
        sb.append("SELECT \n" +
                "\t t2.warehouseId warehouseId, t3.`name` warehouseName\n" +
                "\t ,(CASE WHEN t3.id='0' THEN '总仓'\n" +
                "\t\t\t\tWHEN t3.type='WareHouseType@join' THEN '代理商'\n" +
                "\t\t\t\tWHEN t3.`name` LIKE '%差异仓%' THEN '差异仓'\n" +
                "\t\t\t\tWHEN t3.parentId IS NULL THEN t3.`name` \n" +
                "\t\t\t\tWHEN t3.classes IN ('WareHouseClasses@Point', 'WareHouseClasses@Cabinet') THEN (SELECT `name` FROM warehouse WHERE id=t3.parentId AND isDelete='0')\n" +
                "\t\t\t\tELSE t3.`name` END) belonging\n" +
                "\t,(CASE WHEN t2.takeType='TakeType@cabinet' THEN '机柜' ELSE \n" +
                "\t\t\t(CASE WHEN t2.takeType='TakeType@express' THEN '快递' ELSE \n" +
                "\t\t\t\t(CASE WHEN t2.takeType='TakeType@point' THEN '网点' ELSE \n" +
                "\t\t\t\t\t(CASE WHEN t2.takeType='TakeType@inventory' THEN '代理商库存' ELSE t2.takeType END) \n" +
                "\t\t\t\tEND) \n" +
                "\t\t\tEND) \n" +
                "\t\tEND) takeType\n" +
                "\t, IFNULL(t1.deliverCount, 0) deliverCount, t2.returnCount returnCount\n");
        sb.append("\t, t2.date_hours dateHours \n");
        sb.append("\tFROM\n" +
                "(SELECT \n" +
                "\ta2.areaId warehouseId, a1.takeType, COUNT(1) deliverCount, DATE_FORMAT(a1.deliverDate, '%Y-%m-%d %H:00:00') date_hours\n" +
                "FROM order_deliver a1, order_basic a2\n" +
                "WHERE a1.orderId=a2.id AND a2.isDelete='0' AND a2.`status`='Common@valid' \n" +
                "AND a1.isDelete='0' AND a1.takeType!='TakeType@inventory' AND a1.deliverDate IS NOT NULL\n");
        sb.append("\tAND a1.deliverDate>= '" + startDateTime + "' AND a1.deliverDate<= '" + endDateTime + "' \n");
        sb.append("\tGROUP BY a2.areaId, a1.takeType, date_hours) t1\n" +
                "RIGHT JOIN\n" +
                "(SELECT \n" +
                "\ta.returnWarehouseId warehouseId, a.returnWarehouseName warehouseName, b.takeType, COUNT(1) returnCount, DATE_FORMAT(a.returnTime, '%Y-%m-%d %H:00:00') date_hours \n" +
                "FROM order_return a, order_deliver b\n" +
                "WHERE a.orderId=b.orderId AND a.isDelete='0' AND b.isDelete='0' AND b.takeType!='TakeType@inventory'\n");
        sb.append("AND a.returnTime>='" + startDateTime + "' AND a.returnTime<= '" + endDateTime +"'\n");
        sb.append("GROUP BY a.returnWarehouseId, b.takeType, date_hours) t2 ON t1.warehouseId=t2.warehouseId AND t1.takeType=t2.takeType AND t1.date_hours=t2.date_hours\n" +
                "LEFT JOIN warehouse t3 ON t2.warehouseId=t3.id AND t3.isDelete='0'");
        return DynamicDBUtil.findList(dbKey, sb.toString());
    }

    /**
     * map to DeviceCountHourEntity
     * @param list
     * @return
     */
    private List<DeviceCountHourEntity> mapToHoursEntities(List<Map<String, Object>> list) {
        List<DeviceCountHourEntity> entities = new ArrayList<>();
        DeviceCountHourEntity entity;
        for (Map<String, Object> m : list) {
            entity = new DeviceCountHourEntity();
            entity.setWarehouseId((Integer) m.get("warehouseId"));
            entity.setWarehouseName((String) m.get("warehouseName"));
            entity.setBelonging((String) m.get("belonging"));
            entity.setTakeType((String) m.get("takeType"));
            entity.setDateHours(DateUtil.defaultDate((String) m.get("dateHours")));
            entity.setDeliverCount((Long) m.get("deliverCount"));
            entity.setReturnCount((Long) m.get("returnCount"));
            entities.add(entity);
        }
        return entities;
    }
}

