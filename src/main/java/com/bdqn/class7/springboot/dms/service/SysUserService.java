package com.bdqn.class7.springboot.dms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bdqn.class7.springboot.dms.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 用户服务接口 */
public interface SysUserService extends IService<SysUser> {
    /** 登录：校验成功返回带 token 的用户信息；失败抛 BusinessException */
    LoginVo login(String username, String password);

    SysUser getLoginInfo(Integer userId);

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class LoginVo {
        private String token;
        private Object user;
    }
}
