package org.thingsboard.server.dao.mes.licheng;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@Data
@ConfigurationProperties("licheng.datasource")
public class LichengDataSouce {
    private String host;
    private String port;
    private String user;
    private String password;
    private String dbname;
}
