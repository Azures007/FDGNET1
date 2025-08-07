package org.thingsboard.server.dao.mes.vo;

import lombok.Data;

/**
 * @Auther: l
 * @Date: 2022/5/14 16:40
 * @Description:
 */
@Data
public class WeightVo {
    private Float value;
    private String unit;
    private String unitStr;
    private String recordUnit;
    private Float recordQty;
}
