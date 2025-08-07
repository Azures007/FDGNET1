package org.thingsboard.server.common.data.mes.mid;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("力诚部门表")
@Table(name = "mid_dept")
public class MidDept {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("id")
    @Column(name = "mid_dept_id")
    private Integer midDeptId;
    @ApiModelProperty("部门id")
    @Column(name = "kd_dept_id")
    private Integer kdDeptId;
    @ApiModelProperty("部门名称")
    @Column(name = "kd_dept_name")
    private String kdDeptName;
    @ApiModelProperty("部门编号")
    @Column(name = "kd_dept_num")
    private String kdDeptNum;
    @ApiModelProperty("组织编号")
    @Column(name = "kd_org_num")
    private String kdOrgNum;
    @ApiModelProperty("创建时间")
    @Column(name = "gmt_create")
    private Date gmtCreate;
    @ApiModelProperty("修改时间")
    @Column(name = "gmt_modified")
    private Date gmtModified;
    @ApiModelProperty("是否删除")
    @Column(name = "is_delete")
    private Integer isDelete;
}
