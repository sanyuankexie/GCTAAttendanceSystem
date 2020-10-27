package org.sanyuankexie.attendance.service;

import org.sanyuankexie.attendance.common.DTO.RecordDTO;
import org.sanyuankexie.attendance.common.exception.CExceptionEnum;
import org.sanyuankexie.attendance.common.exception.ServiceException;
import org.sanyuankexie.attendance.mapper.AttendanceRecordMapper;
import org.sanyuankexie.attendance.model.AttendanceRecord;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class AttendanceRecordService {
    @Resource
    private UserService userService;

    @Resource
    private AttendanceRecordMapper recordMapper;

    public boolean isOnlineByUserId(Long userId) {
        return getOnlineRecordByUserId(userId) != null;
    }

    public AttendanceRecord getOnlineRecordByUserId(Long userId) {
        if (userService.getUserByUserId(userId) == null)
            throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST);
        return recordMapper.selectByUserIdAndStatus(userId, 1);
    }

    public void insert(AttendanceRecord record) {
        recordMapper.insert(record);
    }

    public void updateById(AttendanceRecord record) {
        recordMapper.updateById(record);
    }

    public List<RecordDTO> selectRecordListByUserId(Long userId) {
        if (userService.getUserByUserId(userId) == null)
            throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<RecordDTO> recordDTOList = recordMapper.selectRecordListByUserId(userId);
        recordDTOList.forEach(
                it -> {
                    if (it.getStart() != null) {
                        it.setStart(sdf.format(new Date((long) it.getStart())));
                    }
                    if (it.getEnd() != null) {
                        it.setEnd(sdf.format(new Date((long) it.getEnd())));
                    }
                    String status = "已签退";
                    if ((int) it.getStatus() == 1) {
                        status = "在线";
                    } else if ((int) it.getStatus() == -1) {
                        status = "被迫下线";
                    }
                    it.setStatus(status);
                }
        );
        return recordDTOList;
    }
}
