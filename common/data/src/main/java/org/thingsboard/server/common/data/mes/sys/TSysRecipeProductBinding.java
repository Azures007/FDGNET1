package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * 配方产品绑定实体类
 * @author: system
 * @date: 2025-01-10
 * @description: 配方产品绑定表实体
 */
@Data
@Entity
@Table(name = "t_sys_recipe_product_binding")
@ApiModel("配方产品绑定类")
@NoArgsConstructor
@AllArgsConstructor
public class TSysRecipeProductBinding {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "binding_id")
    @ApiModelProperty("绑定ID")
    private Integer bindingId;

    @ApiModelProperty("配方ID")
    @Column(name = "recipe_id", nullable = false)
    private Integer recipeId;

    @ApiModelProperty("产品名称")
    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @ApiModelProperty("产品编码")
    @Column(name = "product_code", nullable = false, length = 50)
    private String productCode;

    @ApiModelProperty("创建时间")
    @Column(name = "create_time")
    private Date createTime;

    // 多对一关系：配方（为避免详情序列化时出现循环/冗余，忽略该字段）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private TSysRecipe recipe;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
    }
}
