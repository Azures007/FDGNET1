package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.mes.sys.TSysUserDetail;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.dao.dto
 * @date 2022/4/11 9:47
 * @Description:
 */
@ApiModel("用户新增,修改模型")
@Data
public class UpdateAndSaveDto {
    @ApiModelProperty(value = "用户id,为null为新增，不为null为修改")
    private String user_id;
    @ApiModelProperty(value = "账号,修改时为null默认不改")
    private String username;
    @ApiModelProperty("用户名,为null默认不改")
    private String first_name;
    @ApiModelProperty("用户密码,仅新增使用")
    private String password;
    @ApiModelProperty("用户状态 0:可用 1：不可用,新增时为null默认为0，修改时为null默认不改")
    private String user_status;
    @ApiModelProperty("角色id,修改时为null默认不改")
    private Integer role_id;
    @ApiModelProperty(value = "修改日期",required = false)
    @Column(name = "updated_time")
    private Date updated_time;
    @ApiModelProperty(value = "修改人",required = false)
    @Column(name = "updated_name")
    private String updated_name;

    @ApiModelProperty(value = "用户详细信息")
    private List<TSysUserDetail> tSysUserDetailList;

}
