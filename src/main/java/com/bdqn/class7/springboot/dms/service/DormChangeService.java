package com.bdqn.class7.springboot.dms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bdqn.class7.springboot.dms.entity.DormChange;

public interface DormChangeService extends IService<DormChange> {
    IPage<DormChange> pageList(Page<DormChange> page, Integer status, String keyword, Integer studentId);

    void applyChange(DormChange dormChange);

    void handleChange(Integer id, Integer status, String handleRemark);
}
