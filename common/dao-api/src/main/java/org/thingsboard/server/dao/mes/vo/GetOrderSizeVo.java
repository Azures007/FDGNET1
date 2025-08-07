package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel("首页订单详情")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetOrderSizeVo implements Serializable {
    @ApiModelProperty("今日任务")
    private Integer currentTask;
    @ApiModelProperty("待生产任务")
    private Integer waitTask;
    @ApiModelProperty("生产中任务")
    private Integer startTask;
    @ApiModelProperty("未生产任务")
    private Integer offTask;
    @ApiModelProperty("已完工任务")
    private Integer endTask;
    @ApiModelProperty("明日任务")
    private Integer tomorrowTask;
    @ApiModelProperty("移交生产中任务")
    private Integer handOverTask;
    @ApiModelProperty("移交待确认任务")
    private Integer waithandOverVerify;
    @ApiModelProperty("转移列表")
    private Integer shiftTask;
}
