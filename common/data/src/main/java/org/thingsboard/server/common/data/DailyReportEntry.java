package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "t_bus_daily_report_entry")
@ApiModel("每日报表明细数据")
public class DailyReportEntry {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("关联的日报表id")
    @Column(name = "dailyreport_id")
    private Integer dailyreportId;

    @ApiModelProperty("频次id，字典")
    @Column(name = "frequency")
    private String frequency;

    @ApiModelProperty("频次值，字典")
    @Column(name = "frequency_value")
    private String frequencyValue;

    @ApiModelProperty("重点项目")
    @Column(name = "important_item")
    private String importantItem;

}
