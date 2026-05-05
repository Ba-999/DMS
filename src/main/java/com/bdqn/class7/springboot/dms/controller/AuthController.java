package com.bdqn.class7.springboot.dms.controller;

import com.bdqn.class7.springboot.dms.common.Result;
import com.bdqn.class7.springboot.dms.entity.LoginUser;
import com.bdqn.class7.springboot.dms.entity.Student;
import com.bdqn.class7.springboot.dms.interceptor.AuthInterceptor;
import com.bdqn.class7.springboot.dms.service.StudentService;
import com.bdqn.class7.springboot.dms.service.SysUserService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final SysUserService sysUserService;
    private final StudentService studentService;
    private final RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/login")
    public Result<SysUserService.LoginVo> login(@RequestBody @Validated LoginDto dto) {
        if ("student".equals(dto.getLoginType())) {
            Student student = studentService.login(dto.getUsername(), dto.getPassword());
            String token = java.util.UUID.randomUUID().toString().replace("-", "");
            AuthInterceptor.saveToken(redisTemplate, token,
                new LoginUser(student.getId(), student.getStudentNo(), student.getName(), student.getPhone(), 2, "student", student.getAvatar()));
            return Result.ok(new SysUserService.LoginVo(token, student));
        }
        return Result.ok(sysUserService.login(dto.getUsername(), dto.getPassword()));
    }

    @PostMapping("/logout")
    public Result<?> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null) redisTemplate.delete(AuthInterceptor.TOKEN_PREFIX + token);
        return Result.ok();
    }

    @GetMapping("/me")
    public Result<?> me() {
        LoginUser current = AuthInterceptor.currentUser();
        if ("student".equals(current.getUserType())) {
            return Result.ok(studentService.getLoginInfo(current.getId()));
        }
        return Result.ok(sysUserService.getLoginInfo(current.getId()));
    }

    @Data
    public static class LoginDto {
        @NotBlank(message = "用户名不能为空") private String username;
        @NotBlank(message = "密码不能为空") private String password;
        private String loginType;
    }
}
