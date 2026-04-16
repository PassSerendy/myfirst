package com.smartOrder.backend.interceptor;

import com.smartOrder.backend.common.util.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {
    
    @Resource
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

//        String testUserId = request.getHeader("X-Test-User-Id");
//        if (testUserId != null) {
//            request.setAttribute("userId", Long.parseLong(testUserId));
//            return true;
//        }
        // 放行 OPTIONS 预检请求（前端跨域时会自动发）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }


        String token = request.getHeader("Authorization");

        // Token 为空
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录，请先登录\",\"data\":null}");
            return false;
        }

        // 去掉 "Bearer " 前缀（如果有的话）
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            // 解析 Token，拿到用户ID
            Long userId = jwtUtil.parseToken(token).get("userId", Long.class);
            // 把用户ID塞进 request 的 attribute 里，Controller 里随时能取
            request.setAttribute("userId", userId);
            return true;
        } catch (Exception e) {
            log.warn("Token解析失败: {}", e.getMessage());
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"Token无效或已过期，请重新登录\",\"data\":null}");
            return false;
        }
    }
}
