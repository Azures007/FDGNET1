package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "t_sync_material_bom")
@ApiModel("物料BOM附表（还原材料）")
@Data
public class TSyncMaterialBom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("物料BOM附表ID")
    private Integer id;

    @Column(name = "parent_id")
    @ApiModelProperty("父级物料ID")
    private Integer parentId;

    @Column(name = "material_id")
    @ApiModelProperty("物料ID")
    private Integer materialId;

    @Column(name = "ratio")
    @ApiModelProperty("比例")
    private BigDecimal ratio;

    @Column(name = "material_name")
    @ApiModelProperty("物料名称")
    private String materialName;


}
