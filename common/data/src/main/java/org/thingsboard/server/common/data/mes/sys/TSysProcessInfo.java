package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.mes.BaseEntity;

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
public class TSysProcessInfo extends BaseEntity {
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
