package com.example.schoolwall.security;

import com.example.schoolwall.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 * JWT拦截器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtTokenUtil jwtTokenUtil;

    private final ObjectMapper objectMapper;

    /**
     * 请求头中的Token键
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * 公开接口路径（即使没有Token也可以访问）
     */
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/user/login",
            "/api/user/register",
            "/api/posts",
            "/api/search",
            "/api/upload/image",
            "/api/posts/image/upload",
            "/api/user/avatar",
            "/api/messages/image/upload",
            "/swagger-ui/",
            "/swagger-resources/",
            "/v3/api-docs/",
            "/doc.html"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String requestUri = request.getRequestURI();
        String method = request.getMethod();

        boolean isPublic = PUBLIC_PATHS.stream().anyMatch(path -> requestUri.startsWith(path));
        
        boolean isGetPublicResource = "GET".equalsIgnoreCase(method) && 
                (requestUri.matches("/api/user/\\d+") ||
                 requestUri.matches("/api/posts/\\d+") ||
                 requestUri.equals("/api/comments"));

        String token = request.getHeader(AUTHORIZATION_HEADER);

        if (token != null && token.startsWith(BEARER_PREFIX)) {
            String tokenValue = token.substring(BEARER_PREFIX.length());
            
            if (!jwtTokenUtil.validateToken(tokenValue)) {
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter writer = response.getWriter();
                writer.write(objectMapper.writeValueAsString(Result.unauthorized("登录已过期，请重新登录")));
                writer.flush();
                writer.close();
                return false;
            }

            Long userId = jwtTokenUtil.getUserIdFromToken(tokenValue);
            if (userId != null) {
                request.setAttribute("userId", userId);
            }
            return true;
        }

        if (isPublic || isGetPublicResource) {
            return true;
        }

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(Result.unauthorized("请先登录")));
        writer.flush();
        writer.close();
        return false;
    }
}