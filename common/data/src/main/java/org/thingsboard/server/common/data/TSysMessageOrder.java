package org.thingsboard.server.common.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Entity
@Data
@ApiModel("订单消息模型")
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_sys_message_order")
public class TSysMessageOrder {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("消息id")
    private Integer id;
    @Column(name = "order_id")
    @ApiModelProperty("订单id")
    private Integer orderId;
    @Column(name = "order_type")
    @ApiModelProperty("订单类型 0=未开工、1=已开工、2=暂停、3=已完工、4=移交中")
    private Integer orderType;
    @Column(name = "order_no")
    @ApiModelProperty("订单号")
    private String orderNo;
    @Column(name = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @ApiModelProperty("下单时间(转成 2022-12-05格式)")
    private Date createdTime;
    @Column(name = "product_standard")
    @ApiModelProperty("产品规格")
    private String productStandard;
    @Column(name = "bill_plan_qty")
    @ApiModelProperty("预期产量")
    private String billPlanQty;
    @ApiModelProperty("移交信息")
    @Column(name = "bot_message")
    private String botMessage;
    @Column(name = "mes_type")
    @ApiModelProperty("消息类型 0：移交订单消息 1：订单变更消息 2：订单暂停 3：订单恢复")
    private String mesType;
    @Column(name = "status_type")
    @ApiModelProperty("状态类型 0:移交 1：普通")
    private String statusType;
    @Column(name = "is_read")
    @ApiModelProperty("是否已读 1:已读 0：未读")
    private String isRead;
    @ApiModelProperty("消息时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "mes_time")
    private Date mesTime;
    @ApiModelProperty("用户id")
    @Column(name = "user_id")
    private String userId;
    @ApiModelProperty("变更信息[str1,str2],str1为变更前，str2为变更后")
    @Column(name = "update_strs")
    private String updateStrs;
    @ApiModelProperty("产品名称")
    @Transient
    private String productName;
    @Transient
    @ApiModelProperty("移交集合")
    private List<BotMessageVo> botMessageVos;

    @ApiModelProperty("工序执行表ID")
    @Column(name = "order_process_id")
    private Integer orderProcessId;

    @ApiModelProperty("移交驳回备注")
    @Column(name = "remark")
    private String remark;

    @Column(name = "execute_process_status")
    @ApiModelProperty("执行工序状态,包括：0=未开工、1=已开工、2=暂停、3=已完工、4=移交中、5=移交驳回")
    private String executeProcessStatus;

    @Override
    public String toString() {
        return "TSysMessageOrder{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", orderType=" + orderType +
                ", orderNo='" + orderNo + '\'' +
                ", createdTime=" + createdTime +
                ", productStandard='" + productStandard + '\'' +
                ", billPlanQty='" + billPlanQty + '\'' +
                ", botMessage='" + botMessage + '\'' +
                ", mesType='" + mesType + '\'' +
                ", statusType='" + statusType + '\'' +
                ", isRead='" + isRead + '\'' +
                ", mesTime=" + mesTime +
                ", userId='" + userId + '\'' +
                ", orderProcessId=" + orderProcessId +
                '}';
    }
}
