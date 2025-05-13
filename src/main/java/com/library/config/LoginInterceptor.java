package com.library.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.entity.vo.ApiResult;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 登录拦截器
 * 用于验证用户是否已登录
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从session中获取登录用户信息
        HttpSession session = request.getSession();
        Object user = session.getAttribute("loginUser");

        // 如果用户已登录，则放行
        if (user != null) {
            return true;
        }

        // 如果用户未登录，则返回未授权错误
        responseJson(response, ApiResult.noauth("请先登录"));
        return false;
    }

    /**
     * 响应JSON数据
     */
    private void responseJson(HttpServletResponse response, ApiResult result) {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        try (PrintWriter writer = response.getWriter()) {
            writer.write(objectMapper.writeValueAsString(result));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}