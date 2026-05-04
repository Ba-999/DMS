package com.bdqn.class7.springboot.dms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdqn.class7.springboot.dms.entity.Repair;
import com.bdqn.class7.springboot.dms.mapper.RepairMapper;
import com.bdqn.class7.springboot.dms.service.RepairService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class RepairServiceImpl extends ServiceImpl<RepairMapper, Repair> implements RepairService {
    @Override
    public IPage<Repair> pageList(Page<Repair> page, Integer status, String keyword) {
        QueryWrapper<Repair> qw = new QueryWrapper<>();
        if (status != null) qw.eq("r.status", status);
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like("s.name", keyword).or().like("d.room_number", keyword));
        }
        qw.orderByDesc("r.apply_time");
        return baseMapper.pageWithDetail(page, qw);
    }
}
