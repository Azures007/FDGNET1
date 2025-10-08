package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.mes.ncWarehouse.NcWarehouse;
import org.thingsboard.server.common.data.mes.sys.TSysRole;

import javax.persistence.Column;
import java.util.List;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.dao.vo
 * @date 2022/4/18 14:49
 * @Description:
 */
@Data
@ApiModel("用户状态")
public class UserStatusVo {
    @ApiModelProperty("是否第一次登陆")
    private Boolean isOneLogin;
    @ApiModelProperty("是否禁用")
    private Boolean byEnabled;
    @ApiModelProperty("用户id")
    private String useId;
    @ApiModelProperty("账号")
    private String username;
    @ApiModelProperty("姓名")
    private String name;
    @ApiModelProperty("登陆时间")
    private String currentDate;
    @ApiModelProperty("上次登陆时间")
    private String frontDate;
    @ApiModelProperty("角色信息")
    private TSysRole tSysRole;
    @ApiModelProperty("当前用户班别信息")
    private UserClassVo userClassVo;

    @ApiModelProperty("仓库")
    List<NcWarehouse> ncWarehouses;
    @ApiModelProperty("产线")
    private String ncVwkname;
}
