package org.thingsboard.server.common.data.web;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.common.data.web
 * @date 2022/4/8 10:17
 * @Description:
 */
public class ResultUtil {
    public static ResponseResult success() {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setErrcode(ErrorCodeEnum.seccess.getCode());
        responseResult.setData(null);
        responseResult.setErrmsg(ErrorCodeEnum.seccess.name());
        return responseResult;
    }

    public static ResponseResult success(Object o) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setErrcode(ErrorCodeEnum.seccess.getCode());
        responseResult.setData(o);
        responseResult.setErrmsg(ErrorCodeEnum.seccess.name());
        return responseResult;
    }

    public static ResponseResult error() {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setErrcode(ErrorCodeEnum.error.getCode());
        responseResult.setData(null);
        responseResult.setErrmsg(ErrorCodeEnum.error.name());
        return responseResult;
    }

    public static ResponseResult error(String msg) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setErrcode(ErrorCodeEnum.error.getCode());
        responseResult.setData(null);
        responseResult.setErrmsg(msg);
        return responseResult;
    }

    public static ResponseResult error(StringBuffer msg) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setErrcode(ErrorCodeEnum.error.getCode());
        responseResult.setData(null);
        responseResult.setErrmsg(msg.toString());
        return responseResult;
    }

    public static ResponseResult error(int code,String msg){
        ResponseResult responseResult = new ResponseResult();
        responseResult.setErrcode(code);
        responseResult.setData(null);
        responseResult.setErrmsg(msg.toString());
        return responseResult;
    }

    public static ResponseResult tokenErrBySx(){
        ResponseResult responseResult = new ResponseResult();
        responseResult.setErrcode(ErrorCodeEnum.TOKEN_ERR_SX.getCode());
        responseResult.setData(null);
        responseResult.setErrmsg("当前登陆令牌无效或已失效请重试");
        return responseResult;
    }

    public static ResponseResult TokenErr(){
        ResponseResult responseResult = new ResponseResult();
        responseResult.setErrcode(ErrorCodeEnum.TOKEN_ERR.getCode());
        responseResult.setData(null);
        responseResult.setErrmsg("当前用户已被禁用");
        return responseResult;
    }

    public static  ResponseResult noClass(){
        ResponseResult responseResult = new ResponseResult();
        responseResult.setErrcode(ErrorCodeEnum.NO_CLASS.getCode());
        responseResult.setData(null);
        responseResult.setErrmsg("当前用户未绑定班组信息，请绑定使用！");
        return responseResult;
    }
}
