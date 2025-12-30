package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author
 * @version V1.0
 * @Package org.thingsboard.server.dao.mes.dto
 * @date 2025/12/29
 * @Description: 报工记录查询DTO
 */
@Data
@ApiModel("报工记录查询条件模型")
public class ReportRecordQueryDto {
    
    @ApiModelProperty(value = "报工时间开始")
    private Date reportTimeStart;
    
    @ApiModelProperty(value = "报工时间结束")
    private Date reportTimeEnd;
    
    @ApiModelProperty(value = "订单号（模糊查询）")
    private String orderNo;
    
    @ApiModelProperty(value = "物料名称（模糊查询）")
    private String materialName;
    
    @ApiModelProperty(value = "物料编码（模糊查询）")
    private String materialNumber;
    
    @ApiModelProperty(value = "产品名称（模糊查询）")
    private String productName;
    
    @ApiModelProperty(value = "产品编码（模糊查询）")
    private String productNumber;
}