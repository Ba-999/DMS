package com.bdqn.class7.springboot.dms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("bus_dorm_change")
public class DormChange {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer studentId;
    private Integer fromDormitoryId;
    private Integer toDormitoryId;
    private String reason;
    private Integer status;
    private String handleRemark;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime applyTime;
    private LocalDateTime handleTime;

    @TableField(exist = false)
    private String studentNo;
    @TableField(exist = false)
    private String studentName;
    @TableField(exist = false)
    private String fromRoomNumber;
    @TableField(exist = false)
    private String toRoomNumber;
    @TableField(exist = false)
    private String fromBuildingName;
    @TableField(exist = false)
    private String toBuildingName;
}
