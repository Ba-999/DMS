package com.bdqn.class7.springboot.dms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bdqn.class7.springboot.dms.common.BusinessException;
import com.bdqn.class7.springboot.dms.common.Result;
import com.bdqn.class7.springboot.dms.entity.CheckIn;
import com.bdqn.class7.springboot.dms.entity.LoginUser;
import com.bdqn.class7.springboot.dms.entity.Repair;
import com.bdqn.class7.springboot.dms.interceptor.AuthInterceptor;
import com.bdqn.class7.springboot.dms.service.CheckInService;
import com.bdqn.class7.springboot.dms.service.RepairService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** 报修管理 */
@RestController
@RequestMapping("/repair")
@RequiredArgsConstructor
public class RepairController {

    private final RepairService repairService;
    private final CheckInService checkInService;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") long current,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(required = false) Integer status,
                          @RequestParam(required = false) String keyword) {
        AuthInterceptor.requireAdmin();
        return Result.ok(repairService.pageList(new Page<>(current, size), status, keyword));
    }

    @GetMapping("/mine")
    public Result<?> mine(@RequestParam(defaultValue = "1") long current,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(required = false) Integer status) {
        LoginUser currentUser = AuthInterceptor.requireStudent();
        return Result.ok(repairService.page(new Page<>(current, size), new LambdaQueryWrapper<Repair>()
                .eq(Repair::getStudentId, currentUser.getId())
                .eq(status != null, Repair::getStatus, status)
                .orderByDesc(Repair::getApplyTime)));
    }

    @PostMapping
    public Result<?> save(@RequestBody Repair entity) {
        AuthInterceptor.requireAdmin();
        if (entity.getStatus() == null) entity.setStatus(0);
        repairService.save(entity);
        return Result.ok();
    }

    @PostMapping("/student")
    public Result<?> studentApply(@RequestBody Repair entity) {
        LoginUser currentUser = AuthInterceptor.requireStudent();
        CheckIn checkIn = checkInService.getOne(new LambdaQueryWrapper<CheckIn>()
                .eq(CheckIn::getStudentId, currentUser.getId())
                .last("limit 1"));
        if (checkIn == null) throw new BusinessException("你当前未入住，无法提交宿舍报修");
        entity.setStudentId(currentUser.getId());
        entity.setDormitoryId(checkIn.getDormitoryId());
        if (entity.getStatus() == null) entity.setStatus(0);
        repairService.save(entity);
        return Result.ok();
    }

    /** 修改状态: 0-待处理 1-维修中 2-已完成 */
    @PutMapping("/status")
    public Result<?> updateStatus(@RequestParam Integer id, @RequestParam Integer status) {
        AuthInterceptor.requireAdmin();
        Repair r = new Repair();
        r.setId(id);
        r.setStatus(status);
        repairService.updateById(r);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) { AuthInterceptor.requireAdmin(); repairService.removeById(id); return Result.ok(); }
}
