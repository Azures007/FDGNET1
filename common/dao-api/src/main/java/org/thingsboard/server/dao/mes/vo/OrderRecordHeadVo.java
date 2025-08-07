package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @Auther: l
 * @Date: 2022/5/10 20:01
 * @Description:
 */
@Data
public class OrderRecordHeadVo {

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("批次号")
    private String bodyLot;

    @ApiModelProperty("订单明细-物料名称")
    private String bodyMaterialName;

    @ApiModelProperty("工序名称")
    private String processName;

    @ApiModelProperty("报工记录列表")
    private List<OrderRecordVo> recordVoList;

    @ApiModelProperty("当前物料列表")
    private Set<String> materials;

    @ApiModelProperty("当前岗位列表")
    private Set<StationVo> stations;

    @ApiModelProperty("当前操作员列表")
    private Set<DevicePersonVo> devicePersonVos;

    @ApiModelProperty("当前机台列表")
    private Set<DeviceVo> deviceVos;
}
