package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("斩拌工序锅数展示")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPotVo {
    @ApiModelProperty("累计投入锅数")
    private int allImportVo;
    @ApiModelProperty("积累投入框数")
    private int allImportFrame;
    @ApiModelProperty("积累投入斗数")
    private int allImportLowa;
    @ApiModelProperty("积累投入数量")
    private Float allImportQty;
//    @ApiModelProperty("计划锅数")
//    private Float bodyPotQty;
//    @ApiModelProperty("计划数量")
//    private Float bodyPlanPrdQty;
//    @ApiModelProperty("积累产出锅数")
//    private Float allExportVo;
//    @ApiModelProperty("积累产出数量")
//    private Float allExportSize;
//    @ApiModelProperty("积累合格品完成比例")
//    private Float allExportSizeRatio;
//    @ApiModelProperty("积累锅数完成比例")
//    private Float allExportVoRatio;
    @ApiModelProperty("本机排手累计投入锅数")
    private int currentImportVo;
    @ApiModelProperty("本机台手投入框数")
    private int currentImportFrame;
    @ApiModelProperty("本机台手投入斗数")
    private int currentImportLowa;
//    @ApiModelProperty("个人产出锅数")
//    private Float currentExportVo;
//    @ApiModelProperty("个人产出数量")
//    private Float currentExportSize;
//    @ApiModelProperty("个人合格品完成比例")
//    private Float currentExportSizeRatio;
//    @ApiModelProperty("个人锅数完成比例")
//    private Float currentExportVoRatio;
}
