package org.thingsboard.server.common.data.web;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.common.data.web
 * @date 2022/4/8 10:15
 * @Description:
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 接口响应类
 */
@ApiModel("接口响应类")
public class ResponseResult<T> {

    @ApiModelProperty("响应代码")
    private int errcode;
    @ApiModelProperty("响应数据")
    private T data;
    @ApiModelProperty("响应消息")
    private String errmsg;
    //私有化构造函数
    public ResponseResult(){}


    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    @Override
    public String toString() {
        return "ResponseResult{" +
                "errcode=" + errcode +
                ", data=" + data +
                ", errmsg='" + errmsg + '\'' +
                '}';
    }
}
