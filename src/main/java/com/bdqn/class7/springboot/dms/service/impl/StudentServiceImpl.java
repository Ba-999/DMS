package com.bdqn.class7.springboot.dms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdqn.class7.springboot.dms.common.BusinessException;
import com.bdqn.class7.springboot.dms.entity.Student;
import com.bdqn.class7.springboot.dms.mapper.StudentMapper;
import com.bdqn.class7.springboot.dms.service.StudentService;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
    @Override
    public Student login(String studentNo, String password) {
        Student student = getOne(new LambdaQueryWrapper<Student>().eq(Student::getStudentNo, studentNo));
        if (student == null) throw new BusinessException("学生不存在");
        if (student.getPassword() == null || !student.getPassword().equals(password)) {
            throw new BusinessException("密码错误");
        }
        return getLoginInfo(student.getId());
    }

    @Override
    public Student getLoginInfo(Integer studentId) {
        Student student = getById(studentId);
        if (student == null) throw new BusinessException("学生不存在");
        student.setUsername(student.getStudentNo());
        student.setNickname(student.getName());
        student.setUserType("student");
        return student;
    }
}
