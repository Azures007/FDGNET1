package org.thingsboard.server.common.data.nc_order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.TBusOrderPPBom;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "t_bus_order_ppbom")
@ApiModel("订单用料清单类")
public class NcTBusOrderPPBom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_ppbom_id")
    @ApiModelProperty("主键ID")
    @JsonIgnore
    private Integer orderPPBomId;

    @Column(name = "nc_cmoid")
    @ApiModelProperty("订单明细id")
    private String cmoid;

    @Column(name = "nc_pk_material")
    @JsonProperty("pk_material")  // 指定JSON属性名
    @ApiModelProperty("物料id")
    private String pkMaterial;

    @Column(name = "material_number")
    @ApiModelProperty("材料编码")
    private String code;

    @Column(name = "material_name")
    @ApiModelProperty("物料名称")
    private String name;

    @Column(name = "material_specification")
    @ApiModelProperty("规格")
    private String materialspec;

    @Column(name = "nc_material_model")
    @ApiModelProperty("型号")
    private String materialModel;

    @Column(name = "unit")
    @ApiModelProperty("单位")
    private String unit;

    @Column(name = "must_qty")
    @ApiModelProperty("需求数量")
    private double nnum;
}
