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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty("主键ID")
    @JsonIgnore
    private Integer id;

    @Column(name = "nc_cwkid")
    @JsonProperty("cwkid")
    @ApiModelProperty("产线id")
    private String cwkid;

    @Column(name = "nc_vwkname")
    @JsonProperty("vwkname")
    @ApiModelProperty("产线名称")
    private String vwkname;

    @Column(name = "nc_vwkcode")
    @JsonProperty("vwkcode")
    @ApiModelProperty("产线编码")
    private String vwkcode;

    @Column(name = "nc_org_id")
    @JsonProperty("org_id")
    @ApiModelProperty("基地id")
    private String orgId;

    @Column(name = "nc_org_name")
    @JsonProperty("org_name")
    @ApiModelProperty("基地名称")
    private String orgName;
} 