package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.DailyReportEntry;
import org.thingsboard.server.common.data.DailyReportItem;
import org.thingsboard.server.common.data.TSysQualityReportItem;
import org.thingsboard.server.common.data.TSysQualityReportPlanRel;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@ApiModel("每日报表模型")
public class DailyReportDto {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("关联的日报表id")
    private Integer dailyreportId;

    @ApiModelProperty("序号")
    private String fseq;

    @ApiModelProperty("频次id，字典")
    private String frequency;

    @ApiModelProperty("频次值，字典")
    private String frequencyValue;

    @ApiModelProperty("重点项目")
    private String importantItem;

    @ApiModelProperty("配置信息")
    private List<DailyReportItem> itemList;
}

