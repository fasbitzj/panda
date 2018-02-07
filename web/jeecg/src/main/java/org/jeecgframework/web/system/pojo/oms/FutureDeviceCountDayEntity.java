package org.jeecgframework.web.system.pojo.oms;

/**
 * Created by jiang.zheng on 2018/1/6.
 */

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "oms_future_device_count_day", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
public class FutureDeviceCountDayEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable=false)
    private Integer id;

    @Column(name = "warehouse_id")
    private Integer warehouseId;

    @Column(name = "warehouse_name")
    private String warehouseName;

    @Column(name = "belonging")
    private String belonging;

    @Column(name = "take_type")
    private String takeType;

    @Column(name = "date_days")
    private Date dateDays;

    @Column(name = "deliver_count")
    private Long deliverCount;

    @Column(name = "return_count")
    private Long returnCount;

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

    public String getTakeType() {
        return takeType;
    }

    public void setTakeType(String takeType) {
        this.takeType = takeType;
    }

    public Date getDateDays() {
        return dateDays;
    }

    public void setDateDays(Date dateDays) {
        this.dateDays = dateDays;
    }

    public Long getDeliverCount() {
        return deliverCount;
    }

    public void setDeliverCount(Long deliverCount) {
        this.deliverCount = deliverCount;
    }

    public Long getReturnCount() {
        return returnCount;
    }

    public void setReturnCount(Long returnCount) {
        this.returnCount = returnCount;
    }

    public String getBelonging() {
        return belonging;
    }

    public void setBelonging(String belonging) {
        this.belonging = belonging;
    }

    @Override
    public String toString() {
        return "DeviceCountHourEntity{" +
                "id=" + id +
                ", warehouseId=" + warehouseId +
                ", warehouseName='" + warehouseName + '\'' +
                ", belonging='" + belonging + '\'' +
                ", takeType='" + takeType + '\'' +
                ", dateDays=" + dateDays +
                ", deliverCount=" + deliverCount +
                ", returnCount=" + returnCount +
                '}';
    }
}
