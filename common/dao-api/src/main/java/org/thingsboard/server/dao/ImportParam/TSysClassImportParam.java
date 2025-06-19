package org.thingsboard.server.dao.ImportParam;

import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.TSysClass;
import org.thingsboard.server.common.data.TSysClassGroupLeaderRel;

import java.util.List;

public class TSysClassImportParam {

    @ApiModelProperty("班别类")
    TSysClass tSysClass;

    @ApiModelProperty("班别和组长关系")
    List<TSysClassGroupLeaderRel> TSysClassGroupLeaderRelLits;

    public TSysClass gettSysClass() {
        return tSysClass;
    }

    public void settSysClass(TSysClass tSysClass) {
        this.tSysClass = tSysClass;
    }

    public List<TSysClassGroupLeaderRel> getTSysClassGroupLeaderRelLits() {
        return TSysClassGroupLeaderRelLits;
    }

    public void setTSysClassGroupLeaderRelLits(List<TSysClassGroupLeaderRel> TSysClassGroupLeaderRelLits) {
        this.TSysClassGroupLeaderRelLits = TSysClassGroupLeaderRelLits;
    }
}
