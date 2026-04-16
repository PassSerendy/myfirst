package com.smartOrder.backend.exception;

import com.smartOrder.backend.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 专门拦截咱们自己的 CustomException
     */
    @ExceptionHandler(CustomException.class)
    public Result<String> handleCustomException(CustomException e) {
        log.error("业务异常: {}", e.getMessage());
        // 把异常里的 message 和 code 取出来，塞进 Result 返回给前端
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 兜底：拦截其他所有没预料到的系统异常（比如 NullPointerException）
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系统未知异常: ", e);
        return Result.error("服务器开小差了，请联系管理员");
    }
}
