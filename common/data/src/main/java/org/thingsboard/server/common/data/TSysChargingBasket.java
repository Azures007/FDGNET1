package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "t_sys_charging_basket")
@ApiModel("料筐表")
public class TSysChargingBasket {
    @ApiModelProperty("料筐id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "charging_basket_id")
    private Integer chargingBasketId;
    @ApiModelProperty("料筐代码")
    @Column(name = "code")
    private String code;
    @ApiModelProperty("备注")
    @Column(name = "br")
    private String br;
    @ApiModelProperty("料筐尺寸")
    @Column(name = "charging_basket_size")
    private String chargingBasketSize;
    @ApiModelProperty("状态0：作废 1：启用")
    @Column(name = "charging_basket_status")
    private String chargingBasketStatus;
    @ApiModelProperty("最大承重")
    @Column(name = "max_bearing")
    private String maxBearing;
    @ApiModelProperty("料框重量")
    @Column(name = "weight")
    private Float weight;
    @ApiModelProperty("料框单位")
    @Column(name = "unit")
    private String unit;
    @ApiModelProperty("创建人")
    @Column(name = "created_name")
    private String createdName;
    @ApiModelProperty("创建日期")
    @Column(name = "created_time")
    private Date createdTime;
    @ApiModelProperty("修改人")
    @Column(name = "updated_name")
    private String updatedName;
    @ApiModelProperty("修改日期")
    @Column(name = "updated_time")
    private Date updatedTime;
}
