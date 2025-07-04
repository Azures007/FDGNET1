package org.thingsboard.server.common.data.nc_workline;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "t_sys_workline")
@ApiModel("生产线")
public class NcWorkline {

    @Id
    @Column(name = "nc_cwkid", nullable = false)
    @JsonProperty("cwkid")
    @ApiModelProperty(name="产线id", required = true)
    private String cwkid;

    @Column(name = "nc_vwkname")
    @JsonProperty("vwkname")
    @ApiModelProperty("产线名称")
    private String vwkname;

    @Column(name = "nc_vwkcode")
    @JsonProperty("vwkcode")
    @ApiModelProperty("产线编码")
    private String vwkcode;

    @Column(name = "nc_pk_org")
    @JsonProperty("pk_org")
    @ApiModelProperty("基地id")
    private String pkOrg;

    @Column(name = "nc_org_name")
    @JsonProperty("org_name")
    @ApiModelProperty("基地名称")
    private String orgName;

    @Column(name = "nc_cdeptid")
    @JsonProperty("cdeptid")
    @ApiModelProperty("生产部门id")
    private String cdeptid;

    @Column(name = "nc_cdeptname")
    @JsonProperty("dept_name")
    @ApiModelProperty("生产部门名称")
    private String cdeptname;

    @ApiModelProperty(name = "状态：生效；失效", required = true)
    @Column(name = "nc_status", nullable = false)
    private String status;
}
