package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.Date;

@ApiModel("工艺路线下工序列表返回类")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CraftProcessListVo {
    @ApiModelProperty("工序Id")
    private Integer processId;

    @ApiModelProperty("工序名称")
    private String processName;

    @ApiModelProperty("工序编号")
    private String processNumber;

    @ApiModelProperty("工序说明")
    private String processDetail;

    @ApiModelProperty("是否启用 0：启用 1：禁用")
    private Integer enabled=0;

    @ApiModelProperty("创建人")
    private String createdUser;

    @ApiModelProperty("创建日期")
    private Date createdTime;

    @ApiModelProperty("修改日期")
    private Date updatedTime;

    @ApiModelProperty("修改人")
    private String updatedUser;

    @ApiModelProperty("工艺路线下的工序排序")
    private Integer sort;

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessNumber() {
        return processNumber;
    }

    public void setProcessNumber(String processNumber) {
        this.processNumber = processNumber;
    }

    public String getProcessDetail() {
        return processDetail;
    }

    public void setProcessDetail(String processDetail) {
        this.processDetail = processDetail;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(String updatedUser) {
        this.updatedUser = updatedUser;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
