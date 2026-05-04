package com.bdqn.class7.springboot.dms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdqn.class7.springboot.dms.common.BusinessException;
import com.bdqn.class7.springboot.dms.entity.LoginUser;
import com.bdqn.class7.springboot.dms.entity.SysUser;
import com.bdqn.class7.springboot.dms.interceptor.AuthInterceptor;
import com.bdqn.class7.springboot.dms.mapper.SysUserMapper;
import com.bdqn.class7.springboot.dms.service.SysUserService;
import org.springframework.stereotype.Service;

import java.util.UUID;

/** 用户服务实现：登录 + CRUD（CRUD 由 ServiceImpl 提供） */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public LoginVo login(String username, String password) {
        SysUser user = getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null) throw new BusinessException("用户不存在");
        if (!user.getPassword().equals(password)) throw new BusinessException("密码错误");
        String token = UUID.randomUUID().toString().replace("-", "");
        AuthInterceptor.TOKEN_POOL.put(token, new LoginUser(user.getId(), user.getUsername(), user.getNickname(), user.getPhone(), user.getRole(), "admin", user.getAvatar()));
        return new LoginVo(token, getLoginInfo(user.getId()));
    }

    @Override
    public SysUser getLoginInfo(Integer userId) {
        SysUser user = getById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        return user;
    }
}
