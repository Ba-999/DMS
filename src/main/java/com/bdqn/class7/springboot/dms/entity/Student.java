package com.bdqn.class7.springboot.dms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/** 学生实体：对应 bus_student */
@Data
@TableName("bus_student")
public class Student {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String studentNo;
    @JsonIgnore
    private String password;
    private String name;
    /** M/F */
    private String gender;
    private String college;
    private String phone;
    private String avatar;
    /** 0-未入住, 1-已入住 */
    private Integer status;
    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private String nickname;
    @TableField(exist = false)
    private String userType;
}
