package com.bdqn.class7.springboot.dms.interceptor;

import com.bdqn.class7.springboot.dms.common.BusinessException;
import com.bdqn.class7.springboot.dms.entity.LoginUser;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 简易登录拦截器：用内存 ConcurrentHashMap 维护 token -> 用户。
 * 请求头需携带 Authorization: <token>。
 * 登录接口会把 token 存入 TOKEN_POOL，登出时移除。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    /** 全局 token 池：生产环境请替换为 Redis 或 JWT */
    public static final ConcurrentHashMap<String, LoginUser> TOKEN_POOL = new ConcurrentHashMap<>();

    /** 当前线程登录用户，便于 Controller/Service 直接取用 */
    public static final ThreadLocal<LoginUser> CURRENT_USER = new ThreadLocal<>();

    public static LoginUser currentUser() {
        LoginUser loginUser = CURRENT_USER.get();
        if (loginUser == null) throw new BusinessException(401, "未登录或登录已过期");
        return loginUser;
    }

    public static LoginUser requireAdmin() {
        LoginUser loginUser = currentUser();
        if (!"admin".equals(loginUser.getUserType())) throw new BusinessException(403, "无权限访问");
        return loginUser;
    }

    public static LoginUser requireStudent() {
        LoginUser loginUser = currentUser();
        if (!"student".equals(loginUser.getUserType())) throw new BusinessException(403, "仅学生可操作");
        return loginUser;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty() || !TOKEN_POOL.containsKey(token)) {
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录或登录已过期\"}");
            return false;
        }
        CURRENT_USER.set(TOKEN_POOL.get(token));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        CURRENT_USER.remove();
    }
}
