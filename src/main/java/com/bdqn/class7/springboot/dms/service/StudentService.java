package com.bdqn.class7.springboot.dms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bdqn.class7.springboot.dms.entity.Student;

public interface StudentService extends IService<Student> {
    Student login(String studentNo, String password);

    Student getLoginInfo(Integer studentId);
}
