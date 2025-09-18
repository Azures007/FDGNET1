package org.thingsboard.server.common.data.mes.sys;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 配方实体类
 * @author: system
 * @date: 2025-01-10
 * @description: 配方管理主表实体
 */
@Data
@Entity
@Table(name = "t_sys_recipe")
@ApiModel("配方类")
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TSysRecipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    @ApiModelProperty("配方ID")
    private Integer recipeId;

    @ApiModelProperty("配方名称")
    @Column(name = "recipe_name", nullable = false, length = 100)
    private String recipeName;

    @ApiModelProperty("配方编号")
    @Column(name = "recipe_code", nullable = false, unique = true, length = 50)
    private String recipeCode;

    @ApiModelProperty("基地名称")
    @Column(name = "org_name", length = 100)
    private String orgName;

    @ApiModelProperty("状态：0-禁用，1-启用")
    @Column(name = "status", length = 10)
    private String status = "1";

    @ApiModelProperty("创建人")
    @Column(name = "creator", length = 50)
    private String creator;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;

    @ApiModelProperty("配方说明")
    @Column(name = "recipe_description", columnDefinition = "TEXT")
    private String recipeDescription;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "update_time")
    private Date updateTime;

    @ApiModelProperty("更新人")
    @Column(name = "update_user", length = 50)
    private String updateUser;

    @ApiModelProperty("基地ID")
    @Column(name = "pk_org", length = 50)
    private String pkOrg;

    // 一对多关系：配方投入设置（列表接口避免懒加载序列化）
    @OneToMany(mappedBy = "recipeId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<TSysRecipeInput> recipeInputs;

    // 一对多关系：配方产品绑定（列表接口避免懒加载序列化）
    @OneToMany(mappedBy = "recipeId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<TSysRecipeProductBinding> productBindings;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        updateTime = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }
}
