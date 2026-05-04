package com.bdqn.class7.springboot.dms.controller;

import com.bdqn.class7.springboot.dms.common.BusinessException;
import com.bdqn.class7.springboot.dms.common.Result;
import com.bdqn.class7.springboot.dms.entity.LoginUser;
import com.bdqn.class7.springboot.dms.entity.Student;
import com.bdqn.class7.springboot.dms.entity.SysUser;
import com.bdqn.class7.springboot.dms.interceptor.AuthInterceptor;
import com.bdqn.class7.springboot.dms.service.StudentService;
import com.bdqn.class7.springboot.dms.service.SysUserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** 当前用户相关：头像上传、资料/密码修改 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final SysUserService sysUserService;
    private final StudentService studentService;

    @Value("${app.upload-dir:./uploads}")
    private String uploadDir;

    /** 头像上传；返回 { url: "/uploads/avatars/xxx.png" } */
    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        LoginUser current = AuthInterceptor.currentUser();
        if (file == null || file.isEmpty()) throw new BusinessException("文件为空");
        if (file.getSize() > 2 * 1024 * 1024) throw new BusinessException("图片不能超过 2MB");
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) throw new BusinessException("只能上传图片");

        String original = file.getOriginalFilename() == null ? "img" : file.getOriginalFilename();
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot > -1) ext = original.substring(dot);
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;

        File dir = new File(uploadDir, "avatars");
        if (!dir.exists() && !dir.mkdirs()) throw new BusinessException("无法创建上传目录");
        File dest = new File(dir, filename);
        file.transferTo(dest.getAbsoluteFile());

        String url = "/uploads/avatars/" + filename;

        if (current != null) {
            if ("student".equals(current.getUserType())) {
                Student update = new Student();
                update.setId(current.getId());
                update.setAvatar(url);
                studentService.updateById(update);
                current.setAvatar(url);
            } else {
                SysUser update = new SysUser();
                update.setId(current.getId());
                update.setAvatar(url);
                sysUserService.updateById(update);
                current.setAvatar(url);
            }
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put("url", url);
        return Result.ok(data);
    }

    /** 更新当前登录用户的昵称 / 手机号 */
    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody ProfileDto dto) {
        LoginUser current = AuthInterceptor.currentUser();
        if ("student".equals(current.getUserType())) {
            Student update = new Student();
            update.setId(current.getId());
            update.setName(dto.getNickname());
            update.setPhone(dto.getPhone());
            studentService.updateById(update);
        } else {
            SysUser update = new SysUser();
            update.setId(current.getId());
            update.setNickname(dto.getNickname());
            update.setPhone(dto.getPhone());
            sysUserService.updateById(update);
        }
        current.setNickname(dto.getNickname());
        current.setPhone(dto.getPhone());
        return Result.ok();
    }

    /** 修改密码（原密码校验通过才可修改） */
    @PutMapping("/password")
    public Result<?> updatePassword(@RequestBody PasswordDto dto) {
        LoginUser current = AuthInterceptor.currentUser();
        if (!StringUtils.hasText(dto.getOldPassword()) || !StringUtils.hasText(dto.getNewPassword())) {
            throw new BusinessException("参数不能为空");
        }
        if (dto.getNewPassword().length() < 6) throw new BusinessException("新密码至少 6 位");

        if ("student".equals(current.getUserType())) {
            Student db = studentService.getById(current.getId());
            if (db == null || !dto.getOldPassword().equals(db.getPassword())) throw new BusinessException("原密码不正确");
            Student update = new Student();
            update.setId(current.getId());
            update.setPassword(dto.getNewPassword());
            studentService.updateById(update);
        } else {
            SysUser db = sysUserService.getById(current.getId());
            if (db == null || !db.getPassword().equals(dto.getOldPassword())) throw new BusinessException("原密码不正确");
            SysUser update = new SysUser();
            update.setId(current.getId());
            update.setPassword(dto.getNewPassword());
            sysUserService.updateById(update);
        }
        return Result.ok();
    }

    @Data public static class ProfileDto { private String nickname; private String phone; }
    @Data public static class PasswordDto { private String oldPassword; private String newPassword; }
}
