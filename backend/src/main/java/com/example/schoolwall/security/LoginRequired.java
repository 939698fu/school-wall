package com.example.schoolwall.security;

import java.lang.annotation.*;

/**
 * 登录验证注解
 * 标记需要登录才能访问的接口
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginRequired {
}
