package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


/**
 * @author 许文言
 * @project youchen_IOTServer
 * @description
 * @date 2025/7/30 9:20:43
 */
@Data
@ApiModel("盘点记录列表条件模型")
public class TSysPdRecordDto {

    @ApiModelProperty("产线id")
    private String cwkid;

    @ApiModelProperty("开始时间")
    private Date startTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    @ApiModelProperty("盘点线边仓")
    private String pdWorkshopName;

    @ApiModelProperty("材料名称")
    private String materialName;


}
