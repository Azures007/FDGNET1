package org.thingsboard.server.common.data.mes.ncInventory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "t_bus_inventory")
@ApiModel("NC仓库库存表")
public class NcInventory {
    @Id
    @Column(name = "bill_id", nullable = false)
    @ApiModelProperty(name="单据ID", required = true)
    private String billId;

    @Column(name = "warehouse_id")
    @ApiModelProperty("仓库id")
    private String warehouseId;

    @Column(name = "warehouse_name")
    @ApiModelProperty("仓库名称")
    private String warehouseName;

    @Column(name = "warehouse_code")
    @ApiModelProperty("仓库编码")
    private String warehouseCode;

    @Column(name = "material_id")
    @ApiModelProperty("物料id")
    private String materialId;

    @Column(name = "material_code")
    @ApiModelProperty("物料编码")
    private String materialCode;

    @Column(name = "material_name")
    @ApiModelProperty("物料名称")
    private String materialName;

    @Column(name = "material_type")
    @ApiModelProperty("物料分类")
    private String materialType;

    @Column(name = "lot")
    @ApiModelProperty("批次")
    private String lot;

    @Column(name = "spec")
    @ApiModelProperty("规格")
    private String spec;

    @Column(name = "unit")
    @ApiModelProperty("单位")
    private String unit;

    @Column(name = "qty")
    @ApiModelProperty("库存量")
    private Float qty;

    @Column(name = "status", nullable = false)
    @ApiModelProperty(name="状态", required = true)
    private String status;

    @Transient
    @ApiModelProperty("是否已盘点 0：否 1：是")
    private Integer byPd;
}
