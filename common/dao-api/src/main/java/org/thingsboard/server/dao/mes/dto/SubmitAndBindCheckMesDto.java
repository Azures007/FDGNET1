package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.thingsboard.server.common.data.mes.bus.TBusOrderBindCode;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("报工提交（产后扫码）")
public class SubmitAndBindCheckMesDto {
    @ApiModelProperty("报工vo")
    private OrderRecordSaveDto saveDto;
    @ApiModelProperty("产后扫码VO")
    private TBusOrderBindCode tBusOrderBindCode;

}
