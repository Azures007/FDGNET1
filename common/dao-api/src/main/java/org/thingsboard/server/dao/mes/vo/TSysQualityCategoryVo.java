package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.mes.sys.TSysQualityCategory;
import org.thingsboard.server.common.data.mes.sys.TSysQualityCategoryConfig;

import java.util.List;

@Data
@ApiModel("质检类目明细")
public class TSysQualityCategoryVo extends TSysQualityCategory {

    @ApiModelProperty("字典版本号")
    private String codeVersionNo;

//    @ApiModelProperty("关键工序")
//    private String keyProcessName;
//
//    @ApiModelProperty("监控方法")
//    private String monitoringMethodName;

    @ApiModelProperty("质检类目配置表")
    List<TSysQualityCategoryConfig> tSysQualityCategoryConfigList;

    public List<TSysQualityCategoryConfig> gettSysQualityCategoryConfigList() {
        return tSysQualityCategoryConfigList;
    }

    public void settSysQualityCategoryConfigList(List<TSysQualityCategoryConfig> tSysQualityCategoryConfigList) {
        this.tSysQualityCategoryConfigList = tSysQualityCategoryConfigList;
    }
}
