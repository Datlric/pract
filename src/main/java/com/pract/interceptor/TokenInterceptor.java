package com.pract.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pract.utils.JwtUtils;
import com.pract.utils.RedisUtils;
import com.pract.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//自定义Token拦截器,验证是否失效，失效直接返回498使前端清空本地token，使其强制登录从而发放新的token
@Component
public class TokenInterceptor implements HandlerInterceptor {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("userToken");
        if (token != null) {
            //1.验证token是否过期
            boolean result = JwtUtils.verify(token);
            if (result) {
                //2.没过期谨防外部假冒token，取redis中token作为验证
                if (redisUtils.get(token) != null) {
                    return true;
                }
            }
        }

        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        try {
            //token失效状态码498
            Result result = Result.error("498", "登陆已过期,请重新登录", null);
            response.getWriter().write(objectMapper.writeValueAsString(result));
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500);
            return false;
        }
        return false;
    }
}
