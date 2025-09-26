package org.thingsboard.server.common.data.mes.bus;

import lombok.Data;
import org.thingsboard.server.common.data.BaseData;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单物料累计状态实体
 */
@Data
@Entity
@Table(name = "t_bus_order_accumulation")
public class TBusOrderAccumulation  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "order_no", length = 50, nullable = false)
    private String orderNo;

    @Column(name = "order_process_id", nullable = false)
    private Integer orderProcessId;

    @Column(name = "order_ppbom_id", nullable = false)
    private Integer orderPpbomId;

    @Column(name = "device_person_group_id", length = 100)
    private String devicePersonGroupId;

    @Column(name = "material_id")
    private Integer materialId;

    @Column(name = "material_number", length = 50, nullable = false)
    private String materialNumber;

    @Column(name = "accumulated_qty", precision = 10, scale = 3)
    private BigDecimal accumulatedQty;

    @Column(name = "last_update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdateTime;

    @Column(name = "created_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;
}

