package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "t_sys_class")
@ApiModel("班别类")
@NoArgsConstructor
@AllArgsConstructor
public class TSysClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private Integer classId;

    @ApiModelProperty("班别名称")
    @Column(name = "name")
    private String name;
    @ApiModelProperty("工序说明")
    @Column(name = "process")
    private String process;
    @ApiModelProperty("组长")
    @Column(name = "group_leader")
    private String groupLeader;
    @ApiModelProperty("组长用户id")
    @Column(name = "group_leader_id")
    private String groupLeaderID;

    @ApiModelProperty("排班")
    @Column(name = "scheduling")
    private String scheduling;
    @ApiModelProperty("排班字典描述")
    @Column(name = "scheduling_code_dsc")
    private String schedulingCodeDsc;

    @ApiModelProperty("组员人数")
    @Column(name = "team_num")
    private String teamNum;

    @ApiModelProperty("是否可用 0：禁用 1：启用")
    @Column(name = "enabled_st")
    private String enabledSt;
    @ApiModelProperty("创建人")
    @Column(name = "crt_user")
    private String crtUser;
    @ApiModelProperty("创建日期")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "crt_time")
    private Date crtTime;
    @ApiModelProperty("修改日期")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "update_time")
    private Date updateTime;
    @ApiModelProperty("修改人")
    @Column(name = "update_user")
    private String updateUser;

    @ApiModelProperty("班别编码")
    @Column(name = "class_number")
    private String classNumber;

    @ApiModelProperty("所属工序id")
    @Column(name = "belong_process_id")
    private Integer belongProcessId;

    @ApiModelProperty("ERP班别编码")
    @Column(name = "class_team_number")
    private String classTeamNumber;

    @ApiModelProperty("车间主任id")
    @Column(name = "workshop_director_id")
    private Integer workshopDirectorId;

    @ApiModelProperty("车间主任")
    @Column(name = "workshop_director")
    private String workshopDirector;

    // 列表的生产组织改为基地，默认为当前登录用户的基地，隐藏生产车间 hhh 2025-07-23
    // 废弃字段：生产组织id、生产车间id；新增基地id
    @ApiModelProperty("生产组织id")
    @Column(name = "kd_org_id")
    private Integer kdOrgId;

    @ApiModelProperty("生产车间id")
    @Column(name = "kd_dept_id")
    private Integer kdDeptId;

    @ApiModelProperty("基地Id")
    @Column(name = "pk_org")
    private String pkOrg;

}
