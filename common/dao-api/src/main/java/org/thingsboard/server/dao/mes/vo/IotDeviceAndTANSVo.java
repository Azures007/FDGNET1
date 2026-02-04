package org.thingsboard.server.dao.mes.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@ToString
@ApiModel("iot设备温湿仪数据")
public class IotDeviceAndTANSVo extends BaseRowModel {

    @ExcelProperty(value = "运行日期", index = 0)
    @ApiModelProperty("时间日期(日期部分)")
    private String byDateDates;
    @ExcelProperty(value = "采集时间", index = 1)
    @ApiModelProperty("时间日期(时间部分)")
    private String byDateTimes;
    @ExcelIgnore
    @ApiModelProperty("时间日期")
    private String byDate;
    @ExcelIgnore
    @ApiModelProperty("时间戳")
    private Long byTs;
    @ExcelProperty(value = "设备编码", index = 2)
    @ApiModelProperty("设备编码")
    private String deviceCode;
    @ExcelProperty(value = "设备名称", index =3)
    @ApiModelProperty("设备名称")
    private String deviceName;
    @ApiModelProperty("温度")
    @ExcelProperty(value = "温度", index =4)
    private BigDecimal byTemp;
    @ApiModelProperty("湿度")
    @ExcelProperty(value = "湿度", index =5)
    private BigDecimal byHemp;


}
