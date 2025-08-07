package org.thingsboard.server.common.data.mes.ncInventory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "t_bus_inventory_inout")
@ApiModel("库存出入记录表")
public class NcInventoryInOut {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_process_history_id")
    @ApiModelProperty("报工记录表行ID")
    private Integer orderProcessHistoryId;

    @Column(name = "bill_id")
    @ApiModelProperty("库存行id")
    private String billId;

    @Column(name = "qty")
    @ApiModelProperty("变化数量")
    private Float qty;
}
