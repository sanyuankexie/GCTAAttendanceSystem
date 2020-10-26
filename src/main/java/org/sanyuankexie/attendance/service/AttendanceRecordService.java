package org.sanyuankexie.attendance.service;

import org.sanyuankexie.attendance.mapper.AttendanceRecordMapper;
import org.sanyuankexie.attendance.model.AttendanceRecord;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AttendanceRecordService {

    @Resource
    private AttendanceRecordMapper recordMapper;

    public boolean isOnlineByUserId(Long userId) {
        return getOnlineRecordByUserId(userId) != null;
    }

    public AttendanceRecord getOnlineRecordByUserId(Long userId) {
        return recordMapper.selectByUserIdAndStatus(userId, 1);
    }

    public void insert(AttendanceRecord record){
        recordMapper.insert(record);
    }

    public void updateById(AttendanceRecord record){
        recordMapper.updateById(record);
    }
}
