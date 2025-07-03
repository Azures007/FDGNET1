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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty("ID")
    @JsonIgnore
    private Integer id;

    @Column(name = "nc_pk_org")
    @ApiModelProperty("基地id")
    @JsonProperty("pk_org")
    private String pkOrg;

    @Column(name = "nc_org_name")
    @ApiModelProperty("基地名称")
    @JsonProperty("org_name")
    private String orgName;

    @ApiModelProperty("是否删除 0:未删除 1:已删除")
    @Column(name = "is_delete")
    @JsonIgnore
    private String isDelete;
}
