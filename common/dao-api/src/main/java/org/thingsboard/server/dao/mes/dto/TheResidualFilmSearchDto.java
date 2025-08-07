package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TheResidualFilmSearchDto {

    @ApiModelProperty("多选物料id")
    List<Integer> materialIds;

    @ApiModelProperty(value = "使用膜长")
    private Float getValue;

    @ApiModelProperty(value = "跳动次数")
    private Float beatTimes;
}
