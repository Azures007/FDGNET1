package org.thingsboard.server.dao.ImportParam;

import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.TSysClass;
import org.thingsboard.server.common.data.TSysClassGroupLeaderRel;
import org.thingsboard.server.common.data.TSysQualityCategory;
import org.thingsboard.server.common.data.TSysQualityCategoryConfig;

import java.util.List;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/6/29 17:35:14
 */
public class TSysQualityCategoryImportParam {

    @ApiModelProperty("质检类目")
    TSysQualityCategory tSysQualityCategory;

    @ApiModelProperty("班别和组长关系")
    List<TSysQualityCategoryConfig> tSysQualityCategoryConfigList;

    public TSysQualityCategory gettSysQualityCategory() {
        return tSysQualityCategory;
    }

    public void settSysQualityCategory(TSysQualityCategory tSysQualityCategory) {
        this.tSysQualityCategory = tSysQualityCategory;
    }

    public List<TSysQualityCategoryConfig> gettSysQualityCategoryConfigList() {
        return tSysQualityCategoryConfigList;
    }

    public void settSysQualityCategoryConfigList(List<TSysQualityCategoryConfig> tSysQualityCategoryConfigList) {
        this.tSysQualityCategoryConfigList = tSysQualityCategoryConfigList;
    }
}
