package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "t_sys_code_dsc_version_rel")
@ApiModel("代码描述公用表")
public class TSysCodeDscVersionRel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ApiModelProperty("版本号UUID")
    @Column(name = "version_no")
    private String versionNo;

    @ApiModelProperty("是否字典组 0：否 1：是")
    @Column(name = "rel_id")
    private Integer relId;

    @ApiModelProperty("创建日期")
    @Column(name = "crt_time")
    private Date crtTime;

}
