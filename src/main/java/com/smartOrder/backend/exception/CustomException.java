package com.smartOrder.backend.exception;

import lombok.Getter;

/**
 * 自定义业务异常类
 * 只要 throw new CustomException("xxx")，就会被全局异常处理器拦截并返回给前端
 */
@Getter
public class CustomException extends RuntimeException {

    private Integer code; // 状态码，默认 400 表示业务错误

    public CustomException(String message) {
        super(message);
        this.code = 400;
    }

    public CustomException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
