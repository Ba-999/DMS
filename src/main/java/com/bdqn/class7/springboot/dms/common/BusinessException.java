package com.bdqn.class7.springboot.dms.common;

/**
 * 业务异常：Service 层抛出，由 GlobalExceptionHandler 统一翻译为 Result.fail。
 */
public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(String msg) { this(500, msg); }

    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public Integer getCode() { return code; }
}
