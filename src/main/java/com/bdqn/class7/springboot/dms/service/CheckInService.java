package com.bdqn.class7.springboot.dms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bdqn.class7.springboot.dms.entity.CheckIn;

public interface CheckInService extends IService<CheckIn> {
    /** 办理入住：增记录+学生状态+宿舍已住人数 */
    void checkIn(CheckIn checkIn);

    /** 退宿：删记录+学生状态回 0+宿舍已住人数减 1 */
    void checkOut(Integer id);

    IPage<CheckIn> pageList(Page<CheckIn> page, String keyword);

    CheckIn getLastByStudentId(Integer studentId);
}
