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
    @Column(name = "nc_pk_stordoc", nullable = false)
    @JsonProperty("pk_stordoc")
    @ApiModelProperty(name="仓库pk", required = true)
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

    @ApiModelProperty(name = "状态：生效；失效", required = true)
    @Column(name = "nc_status", nullable = false)
    private String status;
}
