package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @Auther: l
 * @Date: 2022/5/10 19:50
 * @Description:
 */
@Data
public class OrderRecordVo {
    @ApiModelProperty("报工历史表id")
    private Integer orderProcessHistoryId;

    @ApiModelProperty("班组名称")
    private String className;

    @ApiModelProperty("班组id")
    private Integer classId;


    @ApiModelProperty("盘点人")
    private String personName;

    @ApiModelProperty("工序名称")
    private String processName;

    @ApiModelProperty("物料ID")
    private Integer materialId;

    @ApiModelProperty("物料编码")
    private String materialNumber;

    @ApiModelProperty("物料名称")
    private String materialName;

    @ApiModelProperty("盘点单位编码")
    private String recordUnit;

    @ApiModelProperty("盘点单位")
    private String recordUnitStr;

    @ApiModelProperty("盘点数量")
    private Float recordQty;

    @ApiModelProperty("手动输入的数量")
    private Float recordManualQty;

    @ApiModelProperty("报工时间")
    private String reportTime;

    @ApiModelProperty("类目类型（类目编码）:数据字典维护，内容包括：原辅料，二级品数量、产后数量;交接班盘点，订单完工盘点")
    private String recordType;

    @ApiModelProperty("绑定的料筐编码")
    private String bindCodeNumber;

    @ApiModelProperty("多选机台号分组标识id")
    private String deviceGroupId;

    @ApiModelProperty("机台名称")
    private String deviceGroups;

    @ApiModelProperty("多选操作员分组标识id")
    private String devicePersonGroupId;

    @ApiModelProperty("机台手名称")
    private String devicePersonGroups;

    @ApiModelProperty("操作员岗位")
    private String devicePersonStation;
    @ApiModelProperty("拉伸膜物料名称")
    private String lsmMaterialName;

    private String station;
    private String devicePersonName;
    private Integer devicePersonId;
    private String deviceName;
    private Integer deviceId;
    @ApiModelProperty("iot公式")
    private String iotMath;

    @ApiModelProperty("iot数量")
    private Integer iotQty;

    @ApiModelProperty("锅数")
    private Integer potNumber;
}
