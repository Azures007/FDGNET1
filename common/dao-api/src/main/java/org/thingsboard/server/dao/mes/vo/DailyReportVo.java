package org.thingsboard.server.dao.mes.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.dao.mes.dto.DailyReportDto;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("每日报表模型")
public class DailyReportVo {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("单据编号（支持模糊查询）")
    private String billNo="";

    @ApiModelProperty("产品编码（支持模糊查询）")
    private String materialCode="";

    @ApiModelProperty("产品名称（支持模糊查询）")
    private String materialName="";

    @ApiModelProperty("方案id")
    private String solutId="";

    @ApiModelProperty("方案名称")
    private String solutName="";

    @ApiModelProperty("车间主任id")
    private String shopManagerId="";

    @ApiModelProperty("车间主任名称")
    private String shopManagerName="";

    @ApiModelProperty("启用/禁用")
    private Integer enabled;

    @ApiModelProperty("创建人")
    private String createdName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("创建时间")
    private LocalDate createdTime;

    @ApiModelProperty("修改人")
    private String updatedName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("修改日期")
    private LocalDate updatedTime;

    @ApiModelProperty("生产线id")
    private String prodLineId;

    @ApiModelProperty("生产线名称")
    private String prodLineName;

    @ApiModelProperty("保存还是提交状态/保存是0,提交是1,复核是2")
    private String status;

    @ApiModelProperty("明细数据列表")
    private List<DailyReportDto> itemList;
}
