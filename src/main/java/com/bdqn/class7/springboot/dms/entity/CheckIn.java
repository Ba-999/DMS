package com.bdqn.class7.springboot.dms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** 入住记录实体：对应 bus_check_in */
@Data
@TableName("bus_check_in")
public class CheckIn {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer studentId;
    private Integer dormitoryId;
    private LocalDate checkInDate;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;

    /* 联表展示字段 */
    @TableField(exist = false) private String studentNo;
    @TableField(exist = false) private String studentName;
    @TableField(exist = false) private String buildingName;
    @TableField(exist = false) private String roomNumber;
}
