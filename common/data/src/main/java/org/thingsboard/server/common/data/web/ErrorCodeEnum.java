package org.thingsboard.server.common.data.web;

/**
 * 错误码
 */
public enum ErrorCodeEnum {
    seccess(200),
    TOKEN_ERR(455),
    TOKEN_ERR_SX(465),
    NO_CLASS(475),
    error(0);
    private int code;

    private ErrorCodeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
