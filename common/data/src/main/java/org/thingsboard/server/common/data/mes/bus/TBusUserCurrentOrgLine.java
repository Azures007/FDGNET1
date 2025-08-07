package org.thingsboard.server.common.data.mes.bus;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "t_bus_user_current_org_line")
public class TBusUserCurrentOrgLine {
    /**
     * 用户id
     */
    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;

    /**
     * 基地id
     */
    @Column(name = "org")
    private String org;

    /**
     * 产线id
     */
    @Column(name = "workline")
    private String workline;
}
