package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author hhh
 * @version V1.0
 * @Package org.thingsboard.server.common.data
 * @date 2022/8/1 20:37
 * @Description:
 */
@Data
@Entity
@Table(name = "t_bus_order_process_person_rel")
@ApiModel("订单报工操作员附表")
public class TBusOrderProcessPersonRel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ApiModelProperty("操作员Id")
    @JoinColumn(name = "device_person_id")
    private Integer devicePersonId;

    @ApiModelProperty("操作员分组标识Id")
    @JoinColumn(name = "device_person_group_id")
    private String devicePersonGroupId;

    @ApiModelProperty("创建人")
    @Column(name = "crt_user")
    private String crtUser;
    @ApiModelProperty("创建日期")
    @Column(name = "crt_time")
    private Date crtTime;
    @ApiModelProperty("修改日期")
    @Column(name = "update_time")
    private Date updateTime;
    @ApiModelProperty("修改人")
    @Column(name = "update_user")
    private String updateUser;

    @ApiModelProperty("工序执行表Id")
    @Column(name = "order_process_id")
    private Integer orderProcessId;
}
