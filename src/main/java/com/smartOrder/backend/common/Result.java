package com.smartOrder.backend.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;//状态码
    private String msg;//提示信息
    private T data;//返回的数据

    //私有化构造方法，不让别人new
    private Result() {}

    //成功，带数据
    public static <T> Result<T> success(T data){
        Result<T> result =new Result<>();
        result.code =200;
        result.msg="操作成功";
        result.data=data;
        return result;
    }

    //成功，不带数据(比如删除成功）
    public static <T> Result <T> success(){
        return success(null);
    }

    //失败，自定义错误信息
    public static <T> Result <T> error(String msg){
        Result<T> result = new Result<>();
        result.code=400;//默认给个400的错误码
        result.msg=msg;
        return result;
    }

    //失败，自定义错误码和错误信息
    public static <T> Result <T> error(Integer code,String msg){
        Result<T> result = new Result<>();
        result.code=code;
        result.msg=msg;
        return result;
    }

}
