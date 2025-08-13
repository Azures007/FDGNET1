package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.mes.BaseEntity;

import javax.persistence.*;

/**
 * 样例类
 * @author: hhh
 * @date: 2025/8/13 15:00
 * @description: 用于开发参考
 */
@Data
@Entity
@Table(name = "t_sys_demo")
public class TSysDemo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("id")
    @Column(name = "demo_id")
    private Integer demoId;

    @ApiModelProperty("样例名称")
    @Column(name = "demo_name")
    private String demoName;

    @ApiModelProperty("样例编码")
    @Column(name = "demo_number")
    private String demoNumber;

    @ApiModelProperty("备注")
    @Column(name = "remark")
    private String remark;

}
