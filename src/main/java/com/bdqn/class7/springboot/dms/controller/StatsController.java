package com.bdqn.class7.springboot.dms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bdqn.class7.springboot.dms.common.Result;
import com.bdqn.class7.springboot.dms.entity.Repair;
import com.bdqn.class7.springboot.dms.entity.Student;
import com.bdqn.class7.springboot.dms.interceptor.AuthInterceptor;
import com.bdqn.class7.springboot.dms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/** 首页统计数据 */
@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {

    private final BuildingService buildingService;
    private final DormitoryService dormitoryService;
    private final StudentService studentService;
    private final CheckInService checkInService;
    private final RepairService repairService;

    @GetMapping("/overview")
    public Result<?> overview() {
        AuthInterceptor.requireAdmin();
        long buildings = buildingService.count();
        long dorms = dormitoryService.count();
        long students = studentService.count();
        long checked = studentService.count(new LambdaQueryWrapper<Student>().eq(Student::getStatus, 1));
        long repairPending = repairService.count(new LambdaQueryWrapper<Repair>().eq(Repair::getStatus, 0));
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("buildings", buildings);
        data.put("dormitories", dorms);
        data.put("students", students);
        data.put("checkedIn", checked);
        data.put("repairPending", repairPending);
        data.put("checkInTotal", checkInService.count());
        return Result.ok(data);
    }
}
