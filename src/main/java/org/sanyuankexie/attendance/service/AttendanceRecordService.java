package org.sanyuankexie.attendance.service;

import org.sanyuankexie.attendance.common.DTO.RecordDTO;
import org.sanyuankexie.attendance.common.exception.CExceptionEnum;
import org.sanyuankexie.attendance.common.exception.ServiceException;
import org.sanyuankexie.attendance.mapper.AttendanceRecordMapper;
import org.sanyuankexie.attendance.model.AttendanceRecord;
import org.sanyuankexie.attendance.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class AttendanceRecordService {
    @Resource
    private UserService userService;

    @Resource
    private AttendanceRecordMapper recordMapper;

    DecimalFormat dft = new DecimalFormat("0.00");

    public AttendanceRecord getOnlineRecordByUserId(Long userId) {
        if (userService.getUserByUserId(userId) == null)
            throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST, userId);
        return recordMapper.selectByUserIdAndStatus(userId, 1);
    }

    public List<RecordDTO> selectRecordListByUserId(Long userId) {
        if (userService.getUserByUserId(userId) == null)
            throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST, userId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<RecordDTO> recordDTOList = recordMapper.selectRecordListByUserId(userId);
        recordDTOList.forEach(
                it -> {
                    if ((int) it.getStatus() == 0) {
//                        System.out.println((Long.getLong(String.valueOf(it.getEnd())) - Long.getLong(String.valueOf(it.getStart()))) / 1000 * 1.0 / 3600);
                        it.setAccumulatedTime(dft.format((Long.parseLong(String.valueOf(it.getEnd())) - Long.parseLong(String.valueOf(it.getStart()))) / 1000 * 1.0 / 3600));
                    }
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

    public RecordDTO getUserStatus(Long userId) {
        User user = userService.getUserByUserId(userId);
        if (user == null)
            throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST, userId);
        AttendanceRecord record = recordMapper.selectByUserIdAndStatus(userId, 1);
        RecordDTO recordDTO = new RecordDTO();
        if (record != null) {
            BeanUtils.copyProperties(record, recordDTO);
            recordDTO.setUserName(user.getName());
            return recordDTO;
        } else {
            recordDTO.setUserId(user.getId());
            recordDTO.setStatus(0);
            recordDTO.setUserName(user.getName());
            return recordDTO;
        }
    }

    public boolean isOnlineByUserId(Long userId) {
        return getOnlineRecordByUserId(userId) != null;
    }

    public void insert(AttendanceRecord record) {
        recordMapper.insert(record);
    }

    public void updateById(AttendanceRecord record) {
        recordMapper.updateById(record);
    }
}
