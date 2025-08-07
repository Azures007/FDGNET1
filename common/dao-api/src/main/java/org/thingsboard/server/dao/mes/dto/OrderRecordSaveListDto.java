package org.thingsboard.server.dao.mes.dto;

import lombok.Data;

import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/5/10 11:08
 * @Description:
 */
@Data
public class OrderRecordSaveListDto {

    List<OrderRecordSaveDto> saveDtoList;

}
