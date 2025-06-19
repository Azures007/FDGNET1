package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "t_sys_code_dsc")
@ApiModel("代码描述公用表")
public class TSysCodeDsc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_id")
    private Integer codeId;

    @ApiModelProperty("参数分类编码")
    @Column(name = "code_cl_id")
    private String codeClId;
    @ApiModelProperty("参数分类描述")
    @Column(name = "code_cl_dsc")
    private String codeClDsc;

    @ApiModelProperty("参数编码")
    @Column(name = "code_value")
    private String codeValue;
    @ApiModelProperty("参数描述")
    @Column(name = "code_dsc")
    private String codeDsc;

    @ApiModelProperty("是否字典组 0：否 1：是")
    @Column(name = "is_group")
    private Integer isGroup;

    @ApiModelProperty("是否可用 0：禁用 1：启用")
    @Column(name = "enabled_st")
    private String enabledSt;
    @ApiModelProperty("创建人")
    @Column(name = "crt_user")
    private String crtUser;
    @ApiModelProperty("创建日期")
    @Column(name = "crt_time")
    private Date crtTime;
    @ApiModelProperty("修改日期")
    @Column(name = "update_time")
    private Date updateTime;
    @ApiModelProperty("修改人")
    @Column(name = "update_user")
    private String updateUser;
}
