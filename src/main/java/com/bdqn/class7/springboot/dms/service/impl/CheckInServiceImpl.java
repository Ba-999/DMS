package com.bdqn.class7.springboot.dms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdqn.class7.springboot.dms.common.BusinessException;
import com.bdqn.class7.springboot.dms.entity.CheckIn;
import com.bdqn.class7.springboot.dms.entity.Dormitory;
import com.bdqn.class7.springboot.dms.entity.Student;
import com.bdqn.class7.springboot.dms.mapper.CheckInMapper;
import com.bdqn.class7.springboot.dms.service.CheckInService;
import com.bdqn.class7.springboot.dms.service.DormitoryService;
import com.bdqn.class7.springboot.dms.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CheckInServiceImpl extends ServiceImpl<CheckInMapper, CheckIn> implements CheckInService {

    private final StudentService studentService;
    private final DormitoryService dormitoryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkIn(CheckIn checkIn) {
        Student stu = studentService.getById(checkIn.getStudentId());
        if (stu == null) throw new BusinessException("学生不存在");
        if (stu.getStatus() != null && stu.getStatus() == 1) throw new BusinessException("该学生已入住");

        Dormitory dorm = dormitoryService.getById(checkIn.getDormitoryId());
        if (dorm == null) throw new BusinessException("宿舍不存在");
        if (dorm.getCurrentNumber() != null && dorm.getCapacity() != null && dorm.getCurrentNumber() >= dorm.getCapacity()) throw new BusinessException("该宿舍已满员");

        if (checkIn.getCheckInDate() == null) checkIn.setCheckInDate(LocalDate.now());
        save(checkIn);

        stu.setStatus(1);
        studentService.updateById(stu);

        dorm.setCurrentNumber(dorm.getCurrentNumber() + 1);
        dormitoryService.updateById(dorm);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkOut(Integer id) {
        CheckIn rec = getById(id);
        if (rec == null) throw new BusinessException("记录不存在");
        removeById(id);

        Student stu = studentService.getById(rec.getStudentId());
        if (stu != null) {
            stu.setStatus(0);
            studentService.updateById(stu);
        }
        Dormitory dorm = dormitoryService.getById(rec.getDormitoryId());
        if (dorm != null && dorm.getCurrentNumber() != null && dorm.getCurrentNumber() > 0) {
            dorm.setCurrentNumber(dorm.getCurrentNumber() - 1);
            dormitoryService.updateById(dorm);
        }
    }

    @Override
    public IPage<CheckIn> pageList(Page<CheckIn> page, String keyword) {
        QueryWrapper<CheckIn> qw = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like("s.name", keyword)
                    .or().like("s.student_no", keyword)
                    .or().like("d.room_number", keyword));
        }
        qw.orderByDesc("c.create_time");
        return baseMapper.pageWithDetail(page, qw);
    }

    @Override
    public CheckIn getLastByStudentId(Integer studentId) {
        return baseMapper.selectLastByStudentId(studentId);
    }
}
