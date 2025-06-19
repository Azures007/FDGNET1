package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mid_org")
@ApiModel("力诚组织表")
public class MidOrg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("id")
    @Column(name = "mid_org_id")
    private Integer midOrgId;
    @ApiModelProperty("组织id")
    @Column(name = "kd_org_id")
    private Integer kdOrgId;
    @ApiModelProperty("组织名称")
    @Column(name = "kd_org_name")
    private String kdOrgName;
    @ApiModelProperty("组织编码")
    @Column(name = "kd_org_num")
    private String kdOrgNum;
    @Column(name = "gmtCreate")
    private Date gmtCreate;
    @Column(name = "gmt_modified")
    private Date gmtModified;
    @Column(name = "is_delete")
    private Integer isDelete;
}
