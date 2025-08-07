package org.thingsboard.server.dao.mes.orderProcess;

import org.thingsboard.server.dao.mes.dto.OrderRecordSaveDto;
import org.thingsboard.server.dao.mes.dto.OrderRecordSaveListDto;

import javax.transaction.Transactional;
import java.text.ParseException;

public interface AppOrderProcessRecordSubmitService {

    //工序报工提交
    @Transactional
    Integer submit(OrderRecordSaveDto saveDto, String userId) throws ParseException;

    //工序报工批量提交
    @Transactional
    void mulSubmit(OrderRecordSaveListDto saveListDto, String userId) throws ParseException;

}
