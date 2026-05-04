package com.bdqn.class7.springboot.dms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bdqn.class7.springboot.dms.common.Result;
import com.bdqn.class7.springboot.dms.entity.CheckIn;
import com.bdqn.class7.springboot.dms.entity.LoginUser;
import com.bdqn.class7.springboot.dms.entity.Student;
import com.bdqn.class7.springboot.dms.interceptor.AuthInterceptor;
import com.bdqn.class7.springboot.dms.service.CheckInService;
import com.bdqn.class7.springboot.dms.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/** 学生管理 */
@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final CheckInService checkInService;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") long current,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) Integer status) {
        AuthInterceptor.requireAdmin();
        LambdaQueryWrapper<Student> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like(Student::getName, keyword)
                    .or().like(Student::getStudentNo, keyword));
        }
        if (status != null) qw.eq(Student::getStatus, status);
        qw.orderByDesc(Student::getId);
        return Result.ok(studentService.page(new Page<>(current, size), qw));
    }

    /** 未入住学生下拉(办理入住用) */
    @GetMapping("/unchecked")
    public Result<?> unchecked() {
        AuthInterceptor.requireAdmin();
        return Result.ok(studentService.list(new LambdaQueryWrapper<Student>().eq(Student::getStatus, 0)));
    }

    @GetMapping("/me/checkin")
    public Result<?> myCheckIn() {
        LoginUser currentUser = AuthInterceptor.requireStudent();
        return Result.ok(checkInService.getLastByStudentId(currentUser.getId()));
    }

    @PostMapping
    public Result<?> save(@RequestBody Student entity) {
        AuthInterceptor.requireAdmin();
        if (entity.getStatus() == null) entity.setStatus(0);
        studentService.save(entity);
        return Result.ok();
    }

    @PutMapping
    public Result<?> update(@RequestBody Student entity) { 
        AuthInterceptor.requireAdmin(); 
        studentService.updateById(entity); 
        return Result.ok(); 
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) { 
        AuthInterceptor.requireAdmin(); 
        studentService.removeById(id); 
        return Result.ok(); 
    }
}
