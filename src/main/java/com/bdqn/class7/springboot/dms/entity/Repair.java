package com.bdqn.class7.springboot.dms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 报修实体：对应 bus_repair */
@Data
@TableName("bus_repair")
public class Repair {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer dormitoryId;
    private Integer studentId;
    private String content;
    /** 0-待处理 1-维修中 2-已完成 */
    private Integer status;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime applyTime;

    /* 联表展示字段 */
    @TableField(exist = false) private String studentName;
    @TableField(exist = false) private String buildingName;
    @TableField(exist = false) private String roomNumber;
}
