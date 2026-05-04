package com.bdqn.class7.springboot.dms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/** 楼宇实体：对应 bus_building */
@Data
@TableName("bus_building")
public class Building {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    /** 男生宿舍 / 女生宿舍 */
    private String type;
    private String remark;
}
