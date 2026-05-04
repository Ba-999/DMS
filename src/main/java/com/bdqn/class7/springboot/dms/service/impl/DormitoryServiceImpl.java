package com.bdqn.class7.springboot.dms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdqn.class7.springboot.dms.entity.Dormitory;
import com.bdqn.class7.springboot.dms.mapper.DormitoryMapper;
import com.bdqn.class7.springboot.dms.service.DormitoryService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DormitoryServiceImpl extends ServiceImpl<DormitoryMapper, Dormitory> implements DormitoryService {

    @Override
    public IPage<Dormitory> pageList(Page<Dormitory> page, Integer buildingId, String roomNumber) {
        QueryWrapper<Dormitory> qw = new QueryWrapper<>();
        if (buildingId != null) qw.eq("d.building_id", buildingId);
        if (StringUtils.hasText(roomNumber)) qw.like("d.room_number", roomNumber);
        return baseMapper.pageWithBuilding(page, qw);
    }
}
