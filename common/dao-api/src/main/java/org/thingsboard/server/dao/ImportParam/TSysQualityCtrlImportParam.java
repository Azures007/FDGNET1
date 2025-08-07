package org.thingsboard.server.dao.ImportParam;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.mes.sys.TSysQualityCtrl;
import org.thingsboard.server.common.data.mes.sys.TSysQualityCtrlDetail;

import java.util.List;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/7/18 15:25:56
 */
@Data
public class TSysQualityCtrlImportParam {


    @ApiModelProperty("质检管控表")
    TSysQualityCtrl tSysQualityCtrl;

    @ApiModelProperty("质检管控明细")
    List<TSysQualityCtrlDetail> tSysQualityCtrlDetailList;

}
