package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.dao.dto.ProcessInfoDto;

import java.util.List;

@ApiModel("App订单详情简要VO")
@Data
public class AppOrderDetailSimpleVo {
    @ApiModelProperty("生产订单号")
    private String billNo;

    @ApiModelProperty("订单状态")
    private String orderStatus;

    @ApiModelProperty("下单日期")
    private String billDate;

    @ApiModelProperty("创建人")
    private String creatorName;

    @ApiModelProperty("生产线")
    private String vwkname;

    @ApiModelProperty("产品编码")
    private String code;
    @ApiModelProperty("产品名称")
    private String name;
    @ApiModelProperty("计划产量(件)")
    private Float planQty;
    @ApiModelProperty("规格")
    private String spec;
    @ApiModelProperty("计划开工日期")
    private String planStartDate;
    @ApiModelProperty("计划完工日期")
    private String planFinishDate;

    @ApiModelProperty("原辅材料")
    private List<MaterialItem> materials;

    @Data
    public static class MaterialItem {
        @ApiModelProperty("物料名称")
        private String name;
        @ApiModelProperty("材料编码")
        private String code;

        @ApiModelProperty("型号")
        private String model;
        @ApiModelProperty("单位")
        private String unit;
        @ApiModelProperty("需求数量")
        private Float qty;
    }


}
