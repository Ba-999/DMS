package com.bdqn.class7.springboot.dms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdqn.class7.springboot.dms.common.BusinessException;
import com.bdqn.class7.springboot.dms.entity.CheckIn;
import com.bdqn.class7.springboot.dms.entity.DormChange;
import com.bdqn.class7.springboot.dms.entity.Dormitory;
import com.bdqn.class7.springboot.dms.entity.Student;
import com.bdqn.class7.springboot.dms.mapper.DormChangeMapper;
import com.bdqn.class7.springboot.dms.service.CheckInService;
import com.bdqn.class7.springboot.dms.service.DormChangeService;
import com.bdqn.class7.springboot.dms.service.DormitoryService;
import com.bdqn.class7.springboot.dms.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DormChangeServiceImpl extends ServiceImpl<DormChangeMapper, DormChange> implements DormChangeService {

    private final StudentService studentService;
    private final DormitoryService dormitoryService;
    private final CheckInService checkInService;

    @Override
    public IPage<DormChange> pageList(Page<DormChange> page, Integer status, String keyword, Integer studentId) {
        QueryWrapper<DormChange> qw = new QueryWrapper<>();
        if (status != null) qw.eq("dc.status", status);
        if (studentId != null) qw.eq("dc.student_id", studentId);
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like("s.name", keyword)
                    .or().like("s.student_no", keyword)
                    .or().like("td.room_number", keyword));
        }
        qw.orderByDesc("dc.apply_time");
        return baseMapper.pageWithDetail(page, qw);
    }

    @Override
    public void applyChange(DormChange dormChange) {
        Student student = studentService.getById(dormChange.getStudentId());
        if (student == null) throw new BusinessException("学生不存在");
        Dormitory targetDorm = dormitoryService.getById(dormChange.getToDormitoryId());
        if (targetDorm == null) throw new BusinessException("目标宿舍不存在");
        if (targetDorm.getCurrentNumber() != null && targetDorm.getCapacity() != null && targetDorm.getCurrentNumber() >= targetDorm.getCapacity()) {
            throw new BusinessException("目标宿舍已满员");
        }
        long pending = count(new LambdaQueryWrapper<DormChange>()
                .eq(DormChange::getStudentId, dormChange.getStudentId())
                .eq(DormChange::getStatus, 0));
        if (pending > 0) throw new BusinessException("你已有待审核的换宿申请");
        if (dormChange.getStatus() == null) dormChange.setStatus(0);
        save(dormChange);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleChange(Integer id, Integer status, String handleRemark) {
        DormChange change = getById(id);
        if (change == null) throw new BusinessException("申请记录不存在");
        if (change.getStatus() == null || change.getStatus() != 0) throw new BusinessException("该申请已处理");

        DormChange update = new DormChange();
        update.setId(id);
        update.setStatus(status);
        update.setHandleRemark(handleRemark);
        update.setHandleTime(LocalDateTime.now());
        updateById(update);

        if (status == null || status != 1) return;

        CheckIn currentCheckIn = checkInService.getOne(new LambdaQueryWrapper<CheckIn>()
                .eq(CheckIn::getStudentId, change.getStudentId())
                .last("limit 1"));
        Dormitory targetDorm = dormitoryService.getById(change.getToDormitoryId());
        if (targetDorm == null) throw new BusinessException("目标宿舍不存在");
        if (targetDorm.getCurrentNumber() != null && targetDorm.getCapacity() != null && targetDorm.getCurrentNumber() >= targetDorm.getCapacity()) throw new BusinessException("目标宿舍已满员");

        if (currentCheckIn != null) {
            Dormitory oldDorm = dormitoryService.getById(currentCheckIn.getDormitoryId());
            currentCheckIn.setDormitoryId(change.getToDormitoryId());
            checkInService.updateById(currentCheckIn);
            if (oldDorm != null && oldDorm.getCurrentNumber() != null && oldDorm.getCurrentNumber() > 0) {
                oldDorm.setCurrentNumber(oldDorm.getCurrentNumber() - 1);
                dormitoryService.updateById(oldDorm);
            }
        } else {
            CheckIn checkIn = new CheckIn();
            checkIn.setStudentId(change.getStudentId());
            checkIn.setDormitoryId(change.getToDormitoryId());
            checkInService.checkIn(checkIn);
            return;
        }

        targetDorm.setCurrentNumber(targetDorm.getCurrentNumber() + 1);
        dormitoryService.updateById(targetDorm);

        Student student = studentService.getById(change.getStudentId());
        if (student != null && (student.getStatus() == null || student.getStatus() == 0)) {
            student.setStatus(1);
            studentService.updateById(student);
        }
    }
}
