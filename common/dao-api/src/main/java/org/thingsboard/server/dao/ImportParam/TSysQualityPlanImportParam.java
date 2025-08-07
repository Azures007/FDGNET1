package org.thingsboard.server.dao.ImportParam;

import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.mes.sys.TSysQualityPlan;
import org.thingsboard.server.common.data.mes.sys.TSysQualityPlanConfig;
import org.thingsboard.server.common.data.mes.sys.TSysQualityPlanJudgment;

import java.util.List;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/7/1 17:36:49
 */
public class TSysQualityPlanImportParam {

    @ApiModelProperty("质检方案")
    TSysQualityPlan tSysQualityPlan;

    @ApiModelProperty("质检判定")
    List<TSysQualityPlanJudgment> tSysQualityPlanJudgmentList;

    @ApiModelProperty("质检配置表")
    List<TSysQualityPlanConfig> tSysQualityPlanConfigList;

    public TSysQualityPlan gettSysQualityPlan() {
        return tSysQualityPlan;
    }

    public void settSysQualityPlan(TSysQualityPlan tSysQualityPlan) {
        this.tSysQualityPlan = tSysQualityPlan;
    }

    public List<TSysQualityPlanJudgment> gettSysQualityPlanJudgmentList() {
        return tSysQualityPlanJudgmentList;
    }

    public void settSysQualityPlanJudgmentList(List<TSysQualityPlanJudgment> tSysQualityPlanJudgmentList) {
        this.tSysQualityPlanJudgmentList = tSysQualityPlanJudgmentList;
    }

    public List<TSysQualityPlanConfig> gettSysQualityPlanConfigList() {
        return tSysQualityPlanConfigList;
    }

    public void settSysQualityPlanConfigList(List<TSysQualityPlanConfig> tSysQualityPlanConfigList) {
        this.tSysQualityPlanConfigList = tSysQualityPlanConfigList;
    }
}
