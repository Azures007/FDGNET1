package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther: hhh
 * @Date: 2022/9/20 16:49
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("报工记录修改")
public class SubmitAndModifySaveDto {

    @ApiModelProperty("报工历史记录表id")
    private Integer orderProcessHistoryId;

    @ApiModelProperty("修改数量")
    private Float modifyQty;


}
