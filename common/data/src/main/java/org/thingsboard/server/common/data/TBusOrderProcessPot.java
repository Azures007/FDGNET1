package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_bus_order_process_pot")
@ApiModel("报工流水表")
public class TBusOrderProcessPot {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    @ApiModelProperty("id")
    private Integer id;
    @ApiModelProperty("同一锅标识")
    @Column(name = "same_flag")
    private Integer sameFlag;
    @ApiModelProperty("工序执行表id")
    @Column(name = "order_process_id")
    private Integer orderProcessId;
    @ApiModelProperty("机台号")
    @Column(name = "device_id")
    private Integer deviceId;
    @ApiModelProperty("机排手")
    @Column(name = "device_person_id")
    private Integer devicePersonId;
    @ApiModelProperty("订单用料清单ID")
    @Column(name = "order_ppbom_id")
    private Integer orderPpbomId;
    @ApiModelProperty("处理时间")
    @Column(name = "created_time")
    private Date createdTime;
    @ApiModelProperty("处理人")
    @Column(name = "created_name")
    private String createdName;

}
