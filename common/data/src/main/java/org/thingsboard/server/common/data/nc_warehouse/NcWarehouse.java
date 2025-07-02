package org.thingsboard.server.common.data.nc_warehouse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "t_sys_warehouse")
@ApiModel("仓库")
public class NcWarehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty("主键ID")
    @JsonIgnore
    private Integer id;

    @Column(name = "nc_pk_stordoc")
    @JsonProperty("pk_stordoc")
    @ApiModelProperty("仓库pk")
    private String pkStordoc;

    @Column(name = "nc_pk_org")
    @JsonProperty("pk_org")
    @ApiModelProperty("基地pk")
    private String pkOrg;

    @Column(name = "nc_name")
    @JsonProperty("name")
    @ApiModelProperty("仓库名称")
    private String name;

    @Column(name = "nc_code")
    @JsonProperty("code")
    @ApiModelProperty("仓库编码")
    private String code;

    @ApiModelProperty("是否删除 0:未删除 1:已删除")
    @Column(name = "is_delete")
    private String isDelete;
} 