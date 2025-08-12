package org.thingsboard.server.common.data.mes;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * 实体基类
 * @author: hhh
 * @date: 2025/08/12
 */
@Data
@MappedSuperclass
public class BaseEntity {

    @ApiModelProperty("是否启用 0：禁用 1：启用")
    @Column(name = "enabled")
    private Integer enabled;

    @ApiModelProperty("创建人")
    @Column(name = "created_user")
    private String createdUser;

    @ApiModelProperty("创建日期")
    @Column(name = "created_time")
    private Date createdTime;

    @ApiModelProperty("修改人")
    @Column(name = "updated_user")
    private String updatedUser;

    @ApiModelProperty("修改日期")
    @Column(name = "updated_time")
    private Date updatedTime;

    //后续处理，需要把所有功能都统一添加该字段
//    @ApiModelProperty("备注")
//    @Column(name = "remark")
//    private Date remark;

}
