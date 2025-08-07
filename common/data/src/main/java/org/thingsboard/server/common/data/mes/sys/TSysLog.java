package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "t_sys_log")
@ApiModel("用户操作日志")
public class TSysLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("id")
    private Integer id;
    @ApiModelProperty("用户id")
    @Column(name = "user_id")
    private String userId;
    @ApiModelProperty("功能")
    @Column(name = "methods")
    private String methods;
    @ApiModelProperty("方式")
    @Column(name = "way")
    private String way;
    @ApiModelProperty("入参")
    @Column(name = "in_param")
    private String inParam;
    @ApiModelProperty("ip")
    @Column(name = "ip")
    private String ip;
    @ApiModelProperty("操作人姓名")
    @Column(name = "created_name")
    private String createdName;
    @ApiModelProperty("创建时间")
    @Column(name = "created_time")
    private Date createdTime;
    @ApiModelProperty("操作人用户名")
    @Column(name = "created_username")
    private String createdUsername;

    @Override
    public String toString() {
        return "TSysLog{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", methods='" + methods + '\'' +
                ", way='" + way + '\'' +
                ", inParam='" + inParam + '\'' +
                ", ip='" + ip + '\'' +
                ", createdName='" + createdName + '\'' +
                ", createdTime=" + createdTime +
                ", createdUsername='" + createdUsername + '\'' +
                '}';
    }
}
