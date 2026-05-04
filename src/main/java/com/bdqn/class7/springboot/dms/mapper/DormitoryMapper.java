package com.bdqn.class7.springboot.dms.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.bdqn.class7.springboot.dms.entity.Dormitory;
import org.apache.ibatis.annotations.Param;

/** 宿舍 Mapper：含自定义联表查询（带楼宇名） */
public interface DormitoryMapper extends BaseMapper<Dormitory> {
    IPage<Dormitory> pageWithBuilding(IPage<Dormitory> page, @Param(Constants.WRAPPER) Wrapper<Dormitory> wrapper);
}
