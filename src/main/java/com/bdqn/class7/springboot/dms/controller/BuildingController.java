package com.bdqn.class7.springboot.dms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bdqn.class7.springboot.dms.common.Result;
import com.bdqn.class7.springboot.dms.entity.Building;
import com.bdqn.class7.springboot.dms.interceptor.AuthInterceptor;
import com.bdqn.class7.springboot.dms.service.BuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/** 楼宇管理 */
@RestController
@RequestMapping("/building")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") long current,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(required = false) String name) {
        LambdaQueryWrapper<Building> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) qw.like(Building::getName, name);
        qw.orderByDesc(Building::getId);
        return Result.ok(buildingService.page(new Page<>(current, size), qw));
    }

    /** 前端下拉框 */
    @GetMapping("/list")
    public Result<?> list() {
        return Result.ok(buildingService.list());
    }

    @PostMapping
    public Result<?> save(@RequestBody Building entity) { AuthInterceptor.requireAdmin(); buildingService.save(entity); return Result.ok(); }

    @PutMapping
    public Result<?> update(@RequestBody Building entity) { AuthInterceptor.requireAdmin(); buildingService.updateById(entity); return Result.ok(); }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) { AuthInterceptor.requireAdmin(); buildingService.removeById(id); return Result.ok(); }
}
