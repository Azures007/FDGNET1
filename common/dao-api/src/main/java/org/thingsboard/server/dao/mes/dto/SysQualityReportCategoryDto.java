package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.mes.sys.TSysQualityReportItem;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.Date;
import java.util.List;

/**
 * @author hhh
 * @version V1.0
 * @Package org.thingsboard.server.dao.dto
 * @date 2022/7/13 11:36
 * @Description:
 */
@Data
//@ApiModel("日报检查项")
public class SysQualityReportCategoryDto {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("频次，字典")
    private String frequency;

    @ApiModelProperty("频次，字典")
    private String frequencyValue;

    @ApiModelProperty("重点项目")
    private String importantItem;



    @ApiModelProperty("启用/禁用")
    private Integer enabled;


    @ApiModelProperty("创建人")
    private String createdName;

    @ApiModelProperty("创建时间")
    private Date createdTime;

    @ApiModelProperty("修改人")
    private String updatedName;

    @ApiModelProperty("修改日期")
    private Date updatedTime;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("配置信息")
    private List<TSysQualityReportItem> itemList;

}
