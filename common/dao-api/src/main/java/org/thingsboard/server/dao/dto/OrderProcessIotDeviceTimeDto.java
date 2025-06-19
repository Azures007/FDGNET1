package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Auther: hhh
 * @Date: 2022/9/25 10:10
 * @Description:
 */
@Data
public class OrderProcessIotDeviceTimeDto {
    @ApiModelProperty("设备id集合")
    private List<Integer> deviceIds;
}