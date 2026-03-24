package com.szm.demo.common;

import java.io.Serial;
import java.io.Serializable;

public class Result<T> implements Serializable {
    @Serial
    private static final long serialVersionUID=1L;

    private Integer code;
    private String message;
    private T data;
    private Boolean success;

    public static <T> Result<T> success(){
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(null);
        result.setSuccess(true);
        return result;
    }
    public static <T> Result<T> success(String message){
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(message);
        result.setData(null);
        result.setSuccess(true);
        return result;
    }
    public static <T> Result<T> success(T data){
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        result.setSuccess(true);
        return result;
    }
    public static <T> Result<T> success(String message,T data){
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(message);
        result.setData(data);
        result.setSuccess(true);
        return result;
    }
    public static <T> Result<T> fail(){
        Result<T> result = new Result<>();
        result.setCode(ResultCode.BUSINESS_ERROR.getCode());
        result.setMessage(ResultCode.BUSINESS_ERROR.getMessage());
        result.setData(null);
        result.setSuccess(false);
        return result;
    }
    public static <T> Result<T> fail(String message){
        Result<T> result = new Result<>();
        result.setCode(ResultCode.BUSINESS_ERROR.getCode());
        result.setMessage(message);
        result.setData(null);
        result.setSuccess(false);
        return result;
    }
    public static <T> Result<T> fail(ResultCode resultCode){
        Result<T> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMessage(resultCode.getMessage());
        result.setData(null);
        result.setSuccess(false);
        return result;
    }
    public static <T> Result<T> fail(ResultCode resultCode,String message){
        Result<T> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMessage(message);
        result.setData(null);
        result.setSuccess(false);
        return result;
    }
    public static <T> Result<T> fail(Integer code, String message){
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        result.setSuccess(false);
        return result;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
