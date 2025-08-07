package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.thingsboard.server.common.data.mes.sys.TSysQualityCtrl;
import org.thingsboard.server.common.data.mes.sys.TSysQualityCtrlDetail;

import java.util.List;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/7/18 11:56:32
 */
@Data
@ApiModel("质检管控明细")
public class TSysQualityCtrlVo extends TSysQualityCtrl {



    List<TSysQualityCtrlDetail> tSysQualityCtrlDetailList;

}
