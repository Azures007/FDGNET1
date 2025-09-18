package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * @author hhh
 * @version V1.0
 * @Package org.thingsboard.server.common.data
 * @Description: 用户详细信息表实体类
 */
@Entity
@Data
@ApiModel("用户详细信息")
@Table(name = "t_sys_user_detail")
public class TSysUserDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_detail_id")
    @ApiModelProperty("用户信息表ID")
    private Integer userDetailId;

    @Column(name = "user_id")
    @ApiModelProperty("用户ID")
    private String userId;

    @Column(name = "nc_pk_org")
    @ApiModelProperty("基地ID")
    private String ncPkOrg;

    @Column(name = "nc_org_name")
    @ApiModelProperty("基地名称")
    private String ncOrgName;

    @Column(name = "nc_cwkid")
    @ApiModelProperty("产线ID")
    private String ncCwkid;

    @Column(name = "nc_vwkname")
    @ApiModelProperty("产线名称")
    private String ncVwkname;

//    @Column(name = "nc_cdeptid")
//    @ApiModelProperty("部门ID-废弃")
//    private String ncCdeptid;
//
//    @Column(name = "nc_cdeptname")
//    @ApiModelProperty("部门名称-废弃")
//    private String ncCdeptname;

    @Column(name = "nc_warehouse_id")
    @ApiModelProperty("仓库ID")
    private String ncWarehouseId;

    @Column(name = "nc_warehouse_name")
    @ApiModelProperty("仓库名称")
    private String ncWarehouseName;



}
