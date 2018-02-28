package org.jeecgframework.web.system.pojo.oms;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by jiang.zheng on 2018/1/2.
 */
@Entity
@Table(name = "oms_warehouse_count_day", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
public class WarehouseCountEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable=false)
    private Integer id;

    @Column(name = "warehouse_id")
    private Integer warehouseId;  // 仓位id

    @Column(name = "warehouse_name")
    private String warehouseName;  // 仓位名称

    @Column(name = "belonging")
    private String belonging;  // 归属仓位名称(归属名，实际可能不存在)

    @Column(name = "date_hours")
    private Date dateHours;   // 时间

    @Column(name = "type_name")
    private String typeName;  // 设备类型名称

    @Column(name = "device_status")
    private String deviceStatus;  // 设备状态

    @Column(name = "device_count")
    private Long deviceCount; // 设备数量

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getBelonging() {
        return belonging;
    }

    public void setBelonging(String belonging) {
        this.belonging = belonging;
    }

    public Date getDateHours() {
        return dateHours;
    }

    public void setDateHours(Date dateHours) {
        this.dateHours = dateHours;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Long getDeviceCount() {
        return deviceCount;
    }

    public void setDeviceCount(Long deviceCount) {
        this.deviceCount = deviceCount;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }
}
