package org.thingsboard.server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thingsboard.server.dao.licheng.LiChengService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 力诚定时器
 */
//@Slf4j
//@EnableScheduling
//@Configuration
public class LichengSyncTimer {

//    @Autowired
//    LiChengService liChengService;

//    @Scheduled(cron = "0 */3 * * * ?")
//    private void configureTasks() {
//        log.info("----------------定时器开始工作----------------");
//        liChengService.midMaterialSync(false);
//        log.info("----------------定时器工作完成----------------");
//    }

}
