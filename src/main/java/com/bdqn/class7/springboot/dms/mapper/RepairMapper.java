package com.bdqn.class7.springboot.dms.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.bdqn.class7.springboot.dms.entity.Repair;
import org.apache.ibatis.annotations.Param;

/** 报修 Mapper */
public interface RepairMapper extends BaseMapper<Repair> {
    IPage<Repair> pageWithDetail(IPage<Repair> page, @Param(Constants.WRAPPER) Wrapper<Repair> wrapper);
}
