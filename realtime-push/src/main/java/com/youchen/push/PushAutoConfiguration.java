package com.youchen.push;

import com.youchen.push.service.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@Configuration
@ComponentScan(basePackages = "com.youchen.push")
@EnableJpaRepositories(basePackages = "com.youchen.push.repository")
@EntityScan(basePackages = "com.youchen.push.entity")
@ConditionalOnProperty(prefix = "push.websocket", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PushAutoConfiguration {

    @Bean(initMethod = "start")
    public WebSocketServer webSocketServer(PushService pushService) {
        return new WebSocketServer(pushService);
    }
}


