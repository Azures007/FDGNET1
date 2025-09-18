package org.thingsboard.server.common.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
@Data
@Entity
@Table(name = "t_bus_daily_report_head")
@ApiModel("每日报表表头数据")
public class DailyReportHead {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("单据编号（支持模糊查询）")
    @Column(name = "bill_no")
    private String billNo="";

    @ApiModelProperty("产品编码（支持模糊查询）")
    @Column(name = "material_code")
    private String materialCode="";

    @ApiModelProperty("产品名称（支持模糊查询）")
    @Column(name = "material_name")
    private String materialName="";

    @ApiModelProperty("方案id")
    @Column(name = "solut_id")
    private String solutId="";

    @ApiModelProperty("方案名称")
    @Column(name = "solut_name")
    private String solutName="";

    @ApiModelProperty("车间主任id")
    @Column(name = "shop_manager_id")
    private String shopManagerId="";

    @ApiModelProperty("车间主任名称")
    @Column(name = "shop_manager_name")
    private String shopManagerName="";

    @ApiModelProperty("创建人")
    @Column(name = "created_name")
    private String createdName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("创建时间")
    @Column(name = "created_time")
    private LocalDate createdTime;

    @ApiModelProperty("修改人")
    @Column(name = "updated_name")
    private String updatedName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("修改日期")
    @Column(name = "updated_time")
    private LocalDate updatedTime;

    @ApiModelProperty("启用/禁用")
    @Column(name = "enabled")
    private Integer enabled;

    @ApiModelProperty("生产线id")
    @Column(name = "prod_line_id")
    private String prodLineId;

    @ApiModelProperty("生产线名称")
    @Column(name = "prod_line_name")
    private String prodLineName;

    @ApiModelProperty("保存还是提交状态/保存是0,提交是1")
    @Column(name = "save_status")
    private String saveStatus;

    @ApiModelProperty("是否提交复核,是1,否是0")
    @Column(name = "submit")
    private String submit;

}
