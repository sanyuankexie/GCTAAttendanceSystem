package org.sanyuankexie.attendance.service;

import org.sanyuankexie.attendance.common.DTO.RecordDTO;
import org.sanyuankexie.attendance.common.DTO.UserStatusDTO;
import org.sanyuankexie.attendance.common.DTO.UserStatusEnum;
import org.sanyuankexie.attendance.common.exception.CExceptionEnum;
import org.sanyuankexie.attendance.common.exception.ServiceException;
import org.sanyuankexie.attendance.mapper.AttendanceRecordMapper;
import org.sanyuankexie.attendance.model.AttendanceRecord;
import org.sanyuankexie.attendance.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${attendance.term}")
    private String term;


    DecimalFormat dft = new DecimalFormat("0.00");

    public AttendanceRecord getOnlineRecordByUserId(Long userId) {
        if (userService.getUserByUserId(userId) == null)
            throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST, userId);
        return recordMapper.selectByUserIdAndStatus(userId, UserStatusEnum.ONLINE.getStatus());
    }

    //查询当前学期
    public List<RecordDTO> selectRecordListByUserId(Long userId){


        return selectRecordListByUserId(userId,this.term);
    }
    public List<RecordDTO> selectRecordListByUserId(Long userId,String term) {
        if (userService.getUserByUserId(userId) == null)
            throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST, userId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<RecordDTO> recordDTOList = recordMapper.selectRecordListByUserId(userId,term);
        recordDTOList.forEach(
                it -> {
                    UserStatusEnum status = it.getStatus();
                    if (  status== UserStatusEnum.OFFLINE) {
                        it.setAccumulatedTime(dft.format((Long.parseLong(String.valueOf(it.getEnd())) - Long.parseLong(String.valueOf(it.getStart()))) / 1000 * 1.0 / 3600));
                    }else if (status==UserStatusEnum.SYSTEM_GIVEN){
                        it.setAccumulatedTime(dft.format(  (long)it.getAccumulatedTime()/ 1000 * 1.0 / 3600));
                    }
                    if (it.getStart() != null) {
                        it.setStart(sdf.format(new Date((long) it.getStart())));
                    }
                    if (it.getEnd() != null) {
                        it.setEnd(sdf.format(new Date((long) it.getEnd())));
                    }
                }
        );
        return recordDTOList;
    }

    public RecordDTO getUserStatus(Long userId) {
        User user = userService.getUserByUserId(userId);
        if (user == null)
            throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST, userId);
        AttendanceRecord record = recordMapper.selectByUserIdAndStatus(userId, UserStatusEnum.ONLINE.getStatus());
        RecordDTO recordDTO = new RecordDTO();
        if (record != null) {
            BeanUtils.copyProperties(record, recordDTO);
            recordDTO.setUserName(user.getName());
            return recordDTO;
        } else {
            recordDTO.setUserId(user.getId());
            recordDTO.setStatus(UserStatusEnum.OFFLINE);
            recordDTO.setUserName(user.getName());
            return recordDTO;
        }
    }

    public UserStatusDTO isOnlineByUserId(Long userId) {
        User user = userService.getUserByUserId(userId);
        if (user == null)
            throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST, userId);
        AttendanceRecord record = recordMapper.selectByUserIdAndStatus(userId, UserStatusEnum.ONLINE.getStatus());
        UserStatusDTO userStatusDTO = new UserStatusDTO();
        if (record != null) {
            BeanUtils.copyProperties(record, userStatusDTO);
            userStatusDTO.setUserName(user.getName());
            return userStatusDTO;
        } else {
            userStatusDTO.setUserId(user.getId());
            userStatusDTO.setStatus(UserStatusEnum.OFFLINE.getStatus());
            userStatusDTO.setUserName(user.getName());
            return userStatusDTO;
        }
    }

    public void insert(AttendanceRecord record) {
        recordMapper.insert(record);
    }

    public void updateById(AttendanceRecord record) {
        recordMapper.updateById(record);
    }

    public List<String> getTerm(Long userId){
        return recordMapper.selectTerm(userId);
    }
}
