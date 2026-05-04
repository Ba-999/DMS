package com.bdqn.class7.springboot.dms.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bdqn.class7.springboot.dms.entity.SysUser;
import com.bdqn.class7.springboot.dms.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 启动后若 sys_user 为空则初始化一个超级管理员：admin / 123456
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysUserService sysUserService;

    @Override
    public void run(String... args) {
        long count = sysUserService.count();
        if (count == 0) {
            SysUser admin = new SysUser();
            admin.setUsername("admin");
            admin.setPassword("123456");
            admin.setNickname("超级管理员");
            admin.setRole(0);
            admin.setPhone("13800000000");
            sysUserService.save(admin);
            log.info("初始化管理员账号: admin / 123456");
        }
    }
}
