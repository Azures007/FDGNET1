package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@ApiModel("Iot区间内取数差返回类")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IotDiffValueVo {

    @ApiModelProperty("iot时间区间内差值")
    private BigInteger diffValue;

    @ApiModelProperty("取数的结束时间")
    private String endTime;

    public BigInteger getDiffValue() {
        return diffValue;
    }

    public void setDiffValue(BigInteger diffValue) {
        this.diffValue = diffValue;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
