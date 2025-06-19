package org.thingsboard.server.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;

@Data
@EqualsAndHashCode
public class ChargingBasketExcel extends BaseRowModel {
    @ExcelProperty("料筐编码")
    private String code;

    @ExcelProperty("料筐尺寸")
    private String chargingBasketSize;

    @ExcelProperty("料筐承重")
    private String maxBearing;

    @ExcelProperty("备注说明")
    private String br;

    @ExcelProperty("料筐重量")
    private Float weight;

    public ChargingBasketExcel() {
    }

    public ChargingBasketExcel(String code, String chargingBasketSize, String maxBearing, String br, Float weight) {
        this.code = code;
        this.chargingBasketSize = chargingBasketSize;
        this.maxBearing = maxBearing;
        this.br = br;
        this.weight = weight;
    }
}
