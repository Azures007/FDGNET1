package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Auther: hhh
 * @Date: 2022/8/15 15:10
 * @Description:
 */
@Data
public class OrderProcessIotSearchDto {

    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("多选机台号编码")
    List<String> deviceCodes;


}
