package com.bdqn.class7.springboot.dms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bdqn.class7.springboot.dms.entity.Dormitory;

public interface DormitoryService extends IService<Dormitory> {
    /** 分页查询(含楼宇名)，可按楼宇过滤 */
    IPage<Dormitory> pageList(Page<Dormitory> page, Integer buildingId, String roomNumber);
}
