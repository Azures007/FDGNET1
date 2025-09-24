package org.thingsboard.server.dao.mes.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.checkerframework.checker.formatter.qual.Format;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ApiModel("订单详情信息")
public class OrderDetailVo {

    @ApiModelProperty("订单ID")
    private Integer orderId;

    @ApiModelProperty("生产订单号")
    private String orderNo;

    @ApiModelProperty("下单时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date billDate;

    @ApiModelProperty("计划员")
    private String planner;

    @ApiModelProperty("订单状态：0=未开工、1=已开工、2=暂停、3=已完工")
    private String orderStatus;
    @ApiModelProperty("订单状态(字典描述)")
    private String orderStatusDsc;

    @ApiModelProperty("生产部门")
    private String bodyPrdDept;

    @ApiModelProperty("预期产量（废弃）")
    private BigDecimal billPlanQty;

    @ApiModelProperty("当前工序-工序执行表")
    private Integer currentProcess;
    @ApiModelProperty("当前工序-工序执行表")
    private String currentProcessName;

    @ApiModelProperty("班组id")
    private Integer classId;
    @ApiModelProperty("班组")
    private String className;

    @ApiModelProperty(value = "处理人（操作人）")
    private Integer personId;
    @ApiModelProperty("处理人（操作人）-工序执行表")
    private String personName;
    @ApiModelProperty("处理人（操作人）-工序执行表")
    private String persionName;

    /*订单明细*/
    @ApiModelProperty("批次")
    private String bodyLot;

    @ApiModelProperty("明细-物料ID")
    private Integer bodyMaterialId;

    @ApiModelProperty("产品编码")
    private String bodyMaterialNumber;

    @ApiModelProperty("产品名称")
    private String bodyMaterialName;

    @ApiModelProperty("规格型号")
    private String bodyMaterialSpecification;

    @ApiModelProperty("是否有工序详情")
    private int hasProcessDtl;

    @ApiModelProperty("是否有移交详情")
    private int hasTransferDtl;

    @ApiModelProperty("工艺路线名称")
    private String craftName;

    @ApiModelProperty("计划开工时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date bodyPlanStartDate;

    @ApiModelProperty("计划完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date bodyPlanFinishDate;

    @ApiModelProperty("计划产量（件）")
    private BigDecimal bodyPlanPrdQty;

    @ApiModelProperty("明细-单位-编码")
    private String bodyUnit;

    @ApiModelProperty("明细-单位")
    private String bodyUnitStr;

    private List<OrderDetailPPBomVo> orderDetailPPBomVoList;

    @ApiModelProperty("单据类型")
    private String billType;

    @ApiModelProperty("是否变更 1:是 0:否")
    private Integer isUpdate;

    @ApiModelProperty("明细-每锅数量")
    private Float bodyOnePotQty;

    @ApiModelProperty("明细-锅数")
    private Integer bodyPotQty;

    @ApiModelProperty("bom编号")
    private String midMoEntryBomNumber;

    @ApiModelProperty("完成时间")
    private String orderFinishDate;

    @ApiModelProperty("挂起时间")
    private String orderPendingDate;

    @ApiModelProperty("生产线")
    private String vwkname;

    @ApiModelProperty("创建人")
    private String createdName;
}
