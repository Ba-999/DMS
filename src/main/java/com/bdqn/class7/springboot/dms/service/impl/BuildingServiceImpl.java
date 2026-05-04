package com.bdqn.class7.springboot.dms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdqn.class7.springboot.dms.entity.Building;
import com.bdqn.class7.springboot.dms.mapper.BuildingMapper;
import com.bdqn.class7.springboot.dms.service.BuildingService;
import org.springframework.stereotype.Service;

@Service
public class BuildingServiceImpl extends ServiceImpl<BuildingMapper, Building> implements BuildingService {}
