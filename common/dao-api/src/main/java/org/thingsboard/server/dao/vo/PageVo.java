package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.dao.vo
 * @date 2022/4/15 14:24
 * @Description:
 */
@ApiModel("分页接口")
@Data
public class PageVo<T> {
    @ApiModelProperty("每页显示数量")
    private int size;
    @ApiModelProperty("当前页码")
    private int current;
    @ApiModelProperty("总数量")
    private int total;
    @ApiModelProperty("显示数据集合")
    private List<T> list;


    public PageVo() {
    }

    public PageVo(Page<T> page) {
            current = page.getNumber();
            size = page.getSize();
            total = Integer.parseInt(String.valueOf(page.getTotalElements()));
            list = page.getContent();
    }

    public PageVo(int size, int current) {
        this.size = size;
        this.current = current;
    }


    public PageVo<T> get(Page<T> page) {
        PageVo<T> pageVo = new PageVo<>();
        pageVo.setCurrent(page.getNumber());
        pageVo.setSize(page.getSize());
        pageVo.setTotal(Integer.parseInt(String.valueOf(page.getTotalElements())));
        pageVo.setList(page.getContent());
        return pageVo;
    }

    @Override
    public String toString() {
        return "PageVo{" +
                "size=" + size +
                ", current=" + current +
                ", total=" + total +
                ", list=" + list +
                '}';
    }
}
