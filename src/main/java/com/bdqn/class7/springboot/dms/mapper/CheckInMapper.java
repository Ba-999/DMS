package com.bdqn.class7.springboot.dms.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.bdqn.class7.springboot.dms.entity.CheckIn;
import org.apache.ibatis.annotations.Param;

/** 入住记录 Mapper：含联表查询学生/宿舍信息 */
public interface CheckInMapper extends BaseMapper<CheckIn> {
    IPage<CheckIn> pageWithDetail(IPage<CheckIn> page, @Param(Constants.WRAPPER) Wrapper<CheckIn> wrapper);

    CheckIn selectLastByStudentId(@Param("studentId") Integer studentId);
}
