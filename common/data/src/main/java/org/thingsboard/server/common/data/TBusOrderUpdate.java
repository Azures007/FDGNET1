package org.thingsboard.server.common.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@ApiModel("订单变更记录表")
@Table(name = "t_bus_order_update")
@Entity
public class TBusOrderUpdate {
    @ApiModelProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @ApiModelProperty("变更时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "created_time")
    private Date createdTime;
    @ApiModelProperty("订单id")
    @Column(name = "order_id")
    private Integer orderId;
    @ApiModelProperty("变更类型 0:新增物料 1:删除物料2:变更订单")
    @Column(name = "update_type")
    private String updateType;
    @ApiModelProperty("变更的物料id集合，多个,隔开")
    @Column(name = "ppbom_ids")
    private String ppbomIds;
    @ApiModelProperty("变更物料修改json")
    @Column(name = "ppbom_json")
    private String ppbomJson;
    @ApiModelProperty("变更订单修改json")
    @Column(name = "order_json")
    private String orderJson;
    @Transient
    @ApiModelProperty("变更的用料明细列表")
    private List<TBusOrderPPBom> tBusOrderPPBomList;
    @Transient
    @ApiModelProperty("变更的订单信息")
    private TBusOrderHead tBusOrderHead;

    @Transient
    @ApiModelProperty("明细-计划生产数量(变更前)")
    private Float bodyPlanPrdOldQty;

}
