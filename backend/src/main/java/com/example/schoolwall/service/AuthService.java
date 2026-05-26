package com.example.schoolwall.service;

import com.example.schoolwall.dto.request.LoginRequest;
import com.example.schoolwall.dto.request.RegisterRequest;
import com.example.schoolwall.dto.response.LoginResponse;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户注册
     */
    void register(RegisterRequest request);

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);
}
