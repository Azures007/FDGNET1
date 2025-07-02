package org.thingsboard.server.common.data.nc_department;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "t_sys_department")
@ApiModel("生产部门")
public class NcDepartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty("主键ID")
    @JsonIgnore
    private Integer id;

    @Column(name = "nc_cdeptid")
    @JsonProperty("cdeptid")
    @ApiModelProperty("生产部门id")
    private String cdeptid;

    @Column(name = "nc_cdeptname")
    @JsonProperty("cdeptname")
    @ApiModelProperty("生产部门名称")
    private String cdeptname;

    @Column(name = "nc_cdeptcode")
    @JsonProperty("cdeptcode")
    @ApiModelProperty("生产部门编码")
    private String cdeptcode;

    @Column(name = "nc_pk_org")
    @JsonProperty("pk_org")
    @ApiModelProperty("基地id")
    private String pkOrg;

    @Column(name = "nc_org_name")
    @JsonProperty("org_name")
    @ApiModelProperty("基地名称")
    private String orgName;

    @ApiModelProperty("是否删除 0:未删除 1:已删除")
    @Column(name = "is_delete")
    private String isDelete;
}
