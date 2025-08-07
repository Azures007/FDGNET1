package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.dao.mes.dto.ProcessInfoDto;

import java.util.List;

@ApiModel("订单详情简要VO")
@Data
public class OrderDetailSimpleVo {
    @ApiModelProperty("生产订单号")
    private String billNo;

    @ApiModelProperty("订单类型")
    private String billType;

    @ApiModelProperty("订单状态")
    private String orderStatus;

    @ApiModelProperty("工艺路线")
    private String craftName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("下单日期")
    private String billDate;

    @ApiModelProperty("创建人")
    private String creatorName;

    @ApiModelProperty("生产线")
    private String vwkname;

    @ApiModelProperty("生产订单编号")
    private String orderNo;

    @ApiModelProperty("物料清单（产品信息）")
    private ProductItem product;

    @ApiModelProperty("用料清单")
    private List<MaterialItem> materials;

    @ApiModelProperty("工序执行情况表")
    private List<ProcessExecuteVo> processExecutes;

    @ApiModelProperty("工艺工序列表")
    private List<ProcessInfoDto> craftProcesses;

    @Data
    public static class ProductItem {
        @ApiModelProperty("产品编码")
        private String code;
        @ApiModelProperty("产品名称")
        private String name;
        @ApiModelProperty("计划产量(件)")
        private Float planQty;
        @ApiModelProperty("计划开工日期")
        private String planStartDate;
        @ApiModelProperty("计划完工日期")
        private String planFinishDate;
    }

    @Data
    public static class MaterialItem {
        @ApiModelProperty("材料编码")
        private String code;
        @ApiModelProperty("物料名称")
        private String name;
        @ApiModelProperty("规格")
        private String spec;
        @ApiModelProperty("型号")
        private String model;
        @ApiModelProperty("单位")
        private String unit;
        @ApiModelProperty("需求数量")
        private Float qty;
    }

    /**
     * 工序执行情况表VO
     */
    @Data
    public static class ProcessExecuteVo {
        @ApiModelProperty("序号")
        private Integer index;
        @ApiModelProperty("报工工序")
        private String processName;
        @ApiModelProperty("报工类别")
        private String processType;
        @ApiModelProperty("报工物料名称")
        private String materialName;
        @ApiModelProperty("报工规格")
        private String materialSpec;
        @ApiModelProperty("报工批次")
        private String lot;
        @ApiModelProperty("单位")
        private String unit;
        @ApiModelProperty("报工数")
        private Float qty;
        @ApiModelProperty("报工组")
        private String className;
        @ApiModelProperty("锅数/批次数")
        private Integer potCount;
        @ApiModelProperty("报工人员")
        private String personName;
        @ApiModelProperty("报工时间")
        private String reportTime;
    }
}
