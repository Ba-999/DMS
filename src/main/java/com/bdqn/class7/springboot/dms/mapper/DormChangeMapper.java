package com.bdqn.class7.springboot.dms.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.bdqn.class7.springboot.dms.entity.DormChange;
import org.apache.ibatis.annotations.Param;

public interface DormChangeMapper extends BaseMapper<DormChange> {
    IPage<DormChange> pageWithDetail(IPage<DormChange> page, @Param(Constants.WRAPPER) Wrapper<DormChange> wrapper);
}
