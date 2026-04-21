package com.szm.demo.common;

public enum ResultCode {

    SUCCESS(200,"操作成功"),
    // 客户端错误 400-499
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    PRECONDITION_FAILED(412,"先决条件错误"),
    VALIDATE_FAILED(422, "参数验证失败"),
    // 业务错误 500-599
    BUSINESS_ERROR(500, "业务处理失败"),
    USER_NOT_EXIST(501, "用户不存在"),
    USER_PASSWORD_ERROR(502, "密码错误"),
    USER_LOCKED(503, "账号已被锁定"),
    DATA_DUPLICATE(504, "数据重复"),
    OPERATION_FAILED(505, "操作失败"),
    // 系统错误 600-699
    SYSTEM_ERROR(600, "系统内部错误"),
    DB_ERROR(601, "数据库错误"),
    NETWORK_ERROR(602, "网络错误"),
    TIMEOUT_ERROR(603, "请求超时"),
    // 游戏内部错误
    MP_NOT_ENOUGH(1001,"MP不足");

    private final Integer code;
    private final String message;
    ResultCode(int code, String message){
        this.code=code;
        this.message=message;
    }
    public Integer getCode(){
        return code;
    }
    public String getMessage(){
        return message;
    }
}
