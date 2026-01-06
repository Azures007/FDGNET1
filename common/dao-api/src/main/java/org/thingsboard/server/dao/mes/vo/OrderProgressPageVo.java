package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Page;

/**
 * 订单进度分页结果
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("订单进度分页结果")
public class OrderProgressPageVo extends PageVo<OrderProgress> {

    @ApiModelProperty("订单数")
    private Integer orderCount;

    @ApiModelProperty("未完成数")
    private Integer unfinishedCount;

    public OrderProgressPageVo() {
        super();
    }

    public OrderProgressPageVo(Page<OrderProgress> page) {
        super(page);
    }

    public OrderProgressPageVo(Page<OrderProgress> page, Integer orderCount, Integer unfinishedCount) {
        super(page);
        this.orderCount = orderCount;
        this.unfinishedCount = unfinishedCount;
    }
}
