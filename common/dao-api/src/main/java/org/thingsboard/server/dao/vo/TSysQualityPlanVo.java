package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.mes.sys.TSysQualityPlan;
import org.thingsboard.server.common.data.mes.sys.TSysQualityPlanConfig;
import org.thingsboard.server.common.data.mes.sys.TSysQualityPlanJudgment;

import java.util.List;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/7/1 17:28:20
 */
@Data
@ApiModel("质检方案明细")
public class TSysQualityPlanVo extends TSysQualityPlan {

    @ApiModelProperty("方案判定表")
    List<TSysQualityPlanJudgment> tSysQualityPlanJudgmentList;

    @ApiModelProperty("方案配置表")
    List<TSysQualityPlanConfig> tSysQualityPlanConfigList;


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
