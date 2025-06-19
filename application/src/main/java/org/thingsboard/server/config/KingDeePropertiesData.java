package org.thingsboard.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 配置文件配置类
 */

@Configuration
@Data
@ConfigurationProperties(prefix = "kingdee")
public class KingDeePropertiesData {

    private ConnectionDate system7;

    private ConnectionDate system8;

    private ConnectionDate system9;

    public ConnectionDate getSystemSource(){
        return system9;
    }

    public ConnectionDate getSystemSource(int type){
        if(type == 105){
            return system7;
        }else if(type == 184){
            return system8;
        }else if(type == 168){
            return system9;
        }
        return system9;
    }

    @Data
    public static class ConnectionDate {
        private String k3Cloud;
        private String databaseId;
        private String user;
        private String password;
        private String webServiceUrl;
    }
}
