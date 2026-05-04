package com.bdqn.class7.springboot.dms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {
    private Integer id;
    private String username;
    private String nickname;
    private String phone;
    private Integer role;
    private String userType;
    private String avatar;
}
