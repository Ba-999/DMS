package com.bdqn.class7.springboot.dms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bdqn.class7.springboot.dms.common.BusinessException;
import com.bdqn.class7.springboot.dms.common.Result;
import com.bdqn.class7.springboot.dms.entity.CheckIn;
import com.bdqn.class7.springboot.dms.entity.DormChange;
import com.bdqn.class7.springboot.dms.entity.LoginUser;
import com.bdqn.class7.springboot.dms.interceptor.AuthInterceptor;
import com.bdqn.class7.springboot.dms.service.CheckInService;
import com.bdqn.class7.springboot.dms.service.DormChangeService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dorm-change")
@RequiredArgsConstructor
public class DormChangeController {

    private final DormChangeService dormChangeService;
    private final CheckInService checkInService;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") long current,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(required = false) Integer status,
                          @RequestParam(required = false) String keyword) {
        AuthInterceptor.requireAdmin();
        return Result.ok(dormChangeService.pageList(new Page<>(current, size), status, keyword, null));
    }

    @GetMapping("/mine")
    public Result<?> mine(@RequestParam(defaultValue = "1") long current,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(required = false) Integer status) {
        LoginUser currentUser = AuthInterceptor.requireStudent();
        return Result.ok(dormChangeService.pageList(new Page<>(current, size), status, null, currentUser.getId()));
    }

    @PostMapping
    public Result<?> apply(@RequestBody DormChange entity) {
        LoginUser currentUser = AuthInterceptor.requireStudent();
        entity.setStudentId(currentUser.getId());
        CheckIn checkIn = checkInService.getOne(new LambdaQueryWrapper<CheckIn>()
                .eq(CheckIn::getStudentId, currentUser.getId())
                .last("limit 1"));
        if (checkIn != null) {
            entity.setFromDormitoryId(checkIn.getDormitoryId());
            if (entity.getToDormitoryId() != null && entity.getToDormitoryId().equals(checkIn.getDormitoryId())) {
                throw new BusinessException("目标宿舍不能与当前宿舍相同");
            }
        }
        dormChangeService.applyChange(entity);
        return Result.ok();
    }

    @PutMapping("/handle")
    public Result<?> handle(@RequestBody HandleDto dto) {
        AuthInterceptor.requireAdmin();
        dormChangeService.handleChange(dto.getId(), dto.getStatus(), dto.getHandleRemark());
        return Result.ok();
    }

    @Data
    public static class HandleDto {
        private Integer id;
        private Integer status;
        private String handleRemark;
    }
}
