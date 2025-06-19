package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

@Data
@ApiModel("订单列表条件模型(后台)")
public class TBusOrderDto {

    @ApiModelProperty("单据编号（支持模糊查询）")
    private String billNo="";

    @ApiModelProperty("订单状态：0=未开工、1=已开工、2=暂停、3=已完工")
    private String orderStatus="";

    @ApiModelProperty("当前工序id")
    private Integer currentProcess=0;

    @ApiModelProperty("处理班别id")
    private Integer classId=0;

    @ApiModelProperty("单据日期-开始")
    private String billDateStart;
    @ApiModelProperty("单据日期-结束")
    private String billDateEnd;

    @ApiModelProperty("计划完工时间-开始")
    private String planStartDateStart;
    @ApiModelProperty("计划完工时间-结束")
    private String planStartDateEnd;

    @ApiModelProperty("匹配工艺路线: 0: 不匹配, 1:匹配")
    private String orderMatching = "";

}
