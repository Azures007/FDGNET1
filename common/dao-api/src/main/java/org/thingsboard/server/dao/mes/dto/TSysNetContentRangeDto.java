package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

/**
 * @author 许文言
 * @project youchen_IOTServer
 * @description
 * @date 2025/8/6 16:07:40
 */

@ApiModel("净含量范围管理表")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TSysNetContentRangeDto {

    @Column(name = "material_code")
    @ApiModelProperty("产品编码")
    private String materialCode;

    @Column(name = "material_name")
    @ApiModelProperty("产品名称")
    private String materialName;

    @Column(name = "material_model")
    @ApiModelProperty("产品规格")
    private String materialModel;

    @Column(name = "status")
    @ApiModelProperty("状态（启用/禁用）")
    private String status;

}