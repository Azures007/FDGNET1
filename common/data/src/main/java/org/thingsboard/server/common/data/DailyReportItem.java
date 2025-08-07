package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "t_bus_daily_report_entry_Item")
@ApiModel("每日报表明细配置信息数据")
public class DailyReportItem {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("关联的每日报表明细id")
    @Column(name = "dailyreport_entry_id")
    private Integer dailyreportEntryId;

    @ApiModelProperty("达成（异常）清况描述")
    @Column(name = "field_name")
    private String fieldName;

    @ApiModelProperty("达成情况")
    @Column(name = "status")
    private String status;

    @ApiModelProperty("下拉列表对应字段值")
    @Column(name = "spilt_value")
    private String spiltValue;

    @ApiModelProperty("达成情况类型id")
    @Column(name = "field_type_id")
    private String fieldTypeId;

    @ApiModelProperty("达成情况类型值")
    @Column(name = "field_type_value")
    private String fieldTypeValue;
}
