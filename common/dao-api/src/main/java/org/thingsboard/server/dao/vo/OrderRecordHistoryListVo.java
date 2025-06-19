package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/7/19 20:07
 * @Description:
 */
@Data
public class OrderRecordHistoryListVo {
    @ApiModelProperty("盘点历史列表")
    List<OrderProcessHistoryVo> historyVos;

    @ApiModelProperty("盘点时间")
    private String reportTime;

    @ApiModelProperty("报工/盘点人员")
    private String personName;

    @ApiModelProperty("班组")
    private String className;
}