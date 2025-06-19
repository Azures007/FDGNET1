package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Auther: l
 * @Date: 2022/5/25 14:55
 * @Description:
 */
@Data
@Entity
@Table(name = "t_sys_craft_material_rel")
@ApiModel("工艺工序关系表")
public class TSysCraftMaterialRel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "craft_material_id")
    private Integer craftMaterialId;
    @ApiModelProperty("工艺id")
    @Column(name = "craft_id")
    private Integer craftId;
    @ApiModelProperty("物料code")
    @Column(name = "material_code")
    private String materialCode;
    @ApiModelProperty("物料id")
    @Column(name = "material_id")
    private Integer materialId;
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