package org.thingsboard.server.dao.vo;

import lombok.Data;

import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/7/19 20:54
 * @Description:
 */
@Data
public class OrderProcessHistory {

    OrderCheckVo orderCheckVo;

    List<OrderRecordHistoryListVo> historyListVos;
}