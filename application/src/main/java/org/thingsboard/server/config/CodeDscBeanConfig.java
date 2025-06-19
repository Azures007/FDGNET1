package org.thingsboard.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.TSysCodeDsc;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.dto.TSysCodeDscDto;
import org.thingsboard.server.dao.tSysCodeDsc.TSysCodeDscService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@Component
public class CodeDscBeanConfig {

    @Autowired
    TSysCodeDscService tSysCodeDscService;

    //此注释为在bean加载之后执行
    @PostConstruct
    public void loadDic() {
        TSysCodeDscDto tSysCodeDscDto=new TSysCodeDscDto();
        tSysCodeDscDto.setEnabledSt(GlobalConstant.enableTrue);
        List<TSysCodeDsc> content = tSysCodeDscService.tSysCodeDscList(0, Integer.MAX_VALUE, tSysCodeDscDto).getContent();
        GlobalConstant.setCodeDscTable(content);

    }
    //在spring容器关闭时，释放
    @PreDestroy
    public void destroy() {
        //系统运行结束
    }
}
