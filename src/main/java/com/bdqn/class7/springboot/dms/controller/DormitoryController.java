package com.bdqn.class7.springboot.dms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bdqn.class7.springboot.dms.common.Result;
import com.bdqn.class7.springboot.dms.entity.Dormitory;
import com.bdqn.class7.springboot.dms.interceptor.AuthInterceptor;
import com.bdqn.class7.springboot.dms.service.DormitoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** 宿舍管理 */
@RestController
@RequestMapping("/dormitory")
@RequiredArgsConstructor
public class DormitoryController {

    private final DormitoryService dormitoryService;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") long current,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(required = false) Integer buildingId,
                          @RequestParam(required = false) String roomNumber) {
        AuthInterceptor.requireAdmin();
        return Result.ok(dormitoryService.pageList(new Page<>(current, size), buildingId, roomNumber));
    }

    @GetMapping("/student/page")
    public Result<?> studentPage(@RequestParam(defaultValue = "1") long current,
                                 @RequestParam(defaultValue = "10") long size,
                                 @RequestParam(required = false) Integer buildingId,
                                 @RequestParam(required = false) String roomNumber) {
        AuthInterceptor.requireStudent();
        return Result.ok(dormitoryService.pageList(new Page<>(current, size), buildingId, roomNumber));
    }

    /** 未满员宿舍下拉(办理入住用) */
    @GetMapping("/available")
    public Result<?> available() {
        return Result.ok(dormitoryService.list(new LambdaQueryWrapper<Dormitory>()
                .apply("current_number < capacity")));
    }

    @PostMapping
    public Result<?> save(@RequestBody Dormitory entity) {
        AuthInterceptor.requireAdmin();
        if (entity.getCurrentNumber() == null) entity.setCurrentNumber(0);
        dormitoryService.save(entity);
        return Result.ok();
    }

    @PutMapping
    public Result<?> update(@RequestBody Dormitory entity) { 
        AuthInterceptor.requireAdmin(); 
        dormitoryService.updateById(entity); 
        return Result.ok(); 
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) { 
        AuthInterceptor.requireAdmin(); 
        dormitoryService.removeById(id); 
        return Result.ok(); 
    }
}
