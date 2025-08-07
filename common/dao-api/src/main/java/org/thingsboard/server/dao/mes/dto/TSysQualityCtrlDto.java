package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/7/18 10:18:55
 */
@Data
@ApiModel("质检管控列表条件模型")
public class TSysQualityCtrlDto {


    @ApiModelProperty("检验开始时间")
    private Date inspectionStartTime;

    @ApiModelProperty("检验结束时间")
    private Date inspectionEndTime;

}
