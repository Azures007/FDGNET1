package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Auther: l
 * @Date: 2022/4/20 11:04
 * @Description:工序表
 */
@Data
@Entity
@Table(name = "t_sys_process_info")
@ApiModel("工序表")
public class TSysProcessInfo {
    @Id
    @Column(name = "process_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer processId;

    @ApiModelProperty("工序名称")
    @Column(name = "process_name")
    private String processName;

    @ApiModelProperty("工序编号")
    @Column(name = "process_number")
    private String processNumber;

    @ApiModelProperty("工序说明")
    @Column(name = "process_detail")
    private String processDetail;

    @ApiModelProperty("是否启用 0：禁用 1：启用")
    @Column(name = "enabled")
    private Integer enabled;

    @ApiModelProperty("创建人")
    @Column(name = "created_user")
    private String createdUser;

    @ApiModelProperty("创建日期")
    @Column(name = "created_time")
    private Date createdTime;

    @ApiModelProperty("修改日期")
    @Column(name = "updated_time")
    private Date updatedTime;

    @ApiModelProperty("修改人")
    @Column(name = "updated_user")
    private String updatedUser;

    @ApiModelProperty("ERP工序标识")
    @Column(name = "erp_process_number")
    private String erpProcessNumber;

    @ApiModelProperty("投入设置属性 0：禁用 1：启用")
    @Column(name = "by_set_import")
    private String bySetImport;

    @ApiModelProperty("指纹认证 0：禁用 1：启用")
    @Column(name = "fingerprint_authentication")
    private String fingerprintAuthentication;

    @ApiModelProperty("产出控制 0：禁用 1：启用")
    @Column(name = "by_set_export")
    private String bySetExport;

    @ApiModelProperty("报工类型")
    @Column(name = "report_type")
    private String reportType;
}
