package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

@Data
@ApiModel("用户班别信息")
public class UserClassVo {
    @ApiModelProperty("班别名")
    private String name;
    @ApiModelProperty("班别id")
    private Integer classId;
    @ApiModelProperty("班长")
    private String groupLeader;
    @ApiModelProperty("组成员数量")
    private int groupSize;
    @ApiModelProperty("工序说明")
    private String process;
    @ApiModelProperty("工序ID")
    private Integer processId;
    @ApiModelProperty("人员id")
    private Integer personId;
    @ApiModelProperty("车间主任")
    private String workshopDirector;
}
