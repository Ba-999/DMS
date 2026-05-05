package com.bdqn.class7.springboot.dms.interceptor;

import com.bdqn.class7.springboot.dms.common.BusinessException;
import com.bdqn.class7.springboot.dms.entity.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    /** Token 在 Redis 中的 key 前缀，30 分钟无操作过期 */
    public static final String TOKEN_PREFIX = "token:";
    public static final long TOKEN_EXPIRE_MINUTES = 30;

    private final RedisTemplate<String, Object> redisTemplate;

    /** 当前线程登录用户，Controller/Service 直接取用 */
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

    /** 登录成功后将 token -> LoginUser 存入 Redis，30 分钟过期 */
    public static void saveToken(RedisTemplate<String, Object> redis, String token, LoginUser user) {
        redis.opsForValue().set(TOKEN_PREFIX + token, user, TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录或登录已过期\"}");
            return false;
        }
        LoginUser loginUser = (LoginUser) redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
        if (loginUser == null) {
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录或登录已过期\"}");
            return false;
        }
        CURRENT_USER.set(loginUser);
        // 每次请求刷新过期时间
        redisTemplate.expire(TOKEN_PREFIX + token, TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        CURRENT_USER.remove();
    }
}
