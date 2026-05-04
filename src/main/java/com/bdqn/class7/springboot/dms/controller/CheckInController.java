package com.bdqn.class7.springboot.dms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bdqn.class7.springboot.dms.common.Result;
import com.bdqn.class7.springboot.dms.entity.CheckIn;
import com.bdqn.class7.springboot.dms.interceptor.AuthInterceptor;
import com.bdqn.class7.springboot.dms.service.CheckInService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** 入住管理 */
@RestController
@RequestMapping("/checkin")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") long current,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(required = false) String keyword) {
        return Result.ok(checkInService.pageList(new Page<>(current, size), keyword));
    }

    @PostMapping
    public Result<?> checkIn(@RequestBody CheckIn entity) {
        AuthInterceptor.requireAdmin();
        checkInService.checkIn(entity);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> checkOut(@PathVariable Integer id) {
        AuthInterceptor.requireAdmin();
        checkInService.checkOut(id);
        return Result.ok();
    }
}
