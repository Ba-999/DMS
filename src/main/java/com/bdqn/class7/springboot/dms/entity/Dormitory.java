package com.bdqn.class7.springboot.dms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/** 宿舍房间实体：对应 bus_dormitory */
@Data
@TableName("bus_dormitory")
public class Dormitory {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer buildingId;
    private String roomNumber;
    private Integer capacity;
    private Integer currentNumber;
    private BigDecimal price;

    /** 非数据库字段：联表查出的楼宇名 */
    @TableField(exist = false)
    private String buildingName;
}
