package com.example.schoolwall.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 统一返回结果类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 成功返回
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data, System.currentTimeMillis());
    }

    /**
     * 成功返回（无数据）
     */
    public static Result<Void> success() {
        return new Result<>(200, "success", null, System.currentTimeMillis());
    }

    /**
     * 成功返回（自定义消息）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data, System.currentTimeMillis());
    }

    /**
     * 参数错误
     */
    public static Result<Void> badRequest(String message) {
        return new Result<>(400, message, null, System.currentTimeMillis());
    }
    /**
     * 参数错误（带详细数据）
     */
    public static <T> Result<T> badRequest(String message, T data) {
        return new Result<>(400, message, data, System.currentTimeMillis());
    }
    /**
     * 未登录
     */
    public static Result<Void> unauthorized(String message) {
        return new Result<>(401, message, null, System.currentTimeMillis());
    }

    /**
     * 未登录（泛型版本）
     */
    @SuppressWarnings("unchecked")
    public static <T> Result<T> unauthorized(String message, T data) {
        return (Result<T>) new Result<>(401, message, null, System.currentTimeMillis());
    }

    /**
     * 无权限
     */
    public static Result<Void> forbidden(String message) {
        return new Result<>(403, message, null, System.currentTimeMillis());
    }

    /**
     * 资源不存在
     */
    public static Result<Void> notFound(String message) {
        return new Result<>(404, message, null, System.currentTimeMillis());
    }

    /**
     * 服务器错误
     */
    public static Result<Void> error(String message) {
        return new Result<>(500, message, null, System.currentTimeMillis());
    }

    /**
     * 自定义错误
     */
    public static Result<Void> error(Integer code, String message) {
        return new Result<>(code, message, null, System.currentTimeMillis());
    }
}