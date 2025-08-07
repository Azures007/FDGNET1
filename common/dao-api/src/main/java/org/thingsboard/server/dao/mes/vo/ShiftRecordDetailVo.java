package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessRecord;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel("转移订单详情VO")
public class ShiftRecordDetailVo {

    @ApiModelProperty("订单号")
    private String orderNo;

    @ApiModelProperty("批次")
    private String bodyLot;

    @ApiModelProperty("班别id")
    private Integer classId;

    @ApiModelProperty("班别名称")
    private String className;

    @ApiModelProperty("明细-物料编码")
    private String bodyMaterialNumber;

    @ApiModelProperty("明细-物料名称")
    private String bodyMaterialName;

    @ApiModelProperty("明细-计划生产数量")
    private Float bodyPlanPrdQty;

    @ApiModelProperty("单位")
    private String bodyUnit;

    @ApiModelProperty("转移人员")
    private String shiftName;

    @ApiModelProperty("接收人员")
    private String acceptName;

    @ApiModelProperty("接收班组")
    private String acceptClassName;

    @ApiModelProperty("接受订单号")
    private String toOrderNo;

    @ApiModelProperty("尾料报工列表")
    List<TBusOrderProcessRecord> tBusOrderProcessRecords;

    @ApiModelProperty("报工列表数量")
    private Integer count;

}
