package com.bdqn.class7.springboot.dms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bdqn.class7.springboot.dms.entity.Repair;

public interface RepairService extends IService<Repair> {
    IPage<Repair> pageList(Page<Repair> page, Integer status, String keyword);
}
