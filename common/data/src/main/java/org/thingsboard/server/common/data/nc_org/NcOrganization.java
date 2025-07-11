package org.thingsboard.server.common.data.nc_org;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "t_sys_organization")
@ApiModel("基地")
public class NcOrganization {
    @Id
    @Column(name = "nc_pk_org", nullable = false)
    @ApiModelProperty(name = "基地id", required = true)
    @JsonProperty("pk_org")
    private String pkOrg;

    @Column(name = "nc_org_name")
    @ApiModelProperty("基地名称")
    @JsonProperty("org_name")
    private String orgName;

    @ApiModelProperty(name = "状态：生效；失效", required = true)
    @Column(name = "nc_status", nullable = false)
    private String status;
}
