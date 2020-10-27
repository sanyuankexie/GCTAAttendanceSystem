package org.sanyuankexie.attendance.service;

import org.sanyuankexie.attendance.common.DTO.RankDTO;
import org.sanyuankexie.attendance.common.exception.CExceptionEnum;
import org.sanyuankexie.attendance.common.exception.ServiceException;
import org.sanyuankexie.attendance.common.helper.TimeHelper;
import org.sanyuankexie.attendance.mapper.UserMapper;
import org.sanyuankexie.attendance.model.AttendanceRank;
import org.sanyuankexie.attendance.model.AttendanceRecord;
import org.sanyuankexie.attendance.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserService {
    @Resource
    private MailService mailService;

    @Resource
    private AttendanceRankService rankService;

    @Resource
    private AttendanceRecordService recordService;

    @Resource
    private UserMapper userMapper;

    public RankDTO signIn(Long userId) {
        User user = getUserByUserId(userId);
        if (user == null) throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST);
        AttendanceRecord onlineRecord = recordService.getOnlineRecordByUserId(userId);
        AttendanceRank rank = rankService.selectByUserIdAndWeek(userId, TimeHelper.getNowWeek());
        // If is first sign in
        if (rank == null) {
            //id, userId, week, totalTime
            rank = new AttendanceRank(
                    String.valueOf(TimeHelper.getNowWeek()) + String.valueOf(userId),
                    userId,
                    TimeHelper.getNowWeek(),
                    0L
            );
            rankService.insert(rank);
        }
        //Judging if Online
        RankDTO rankDTO = new RankDTO();
        if (onlineRecord == null) {
            //id, userId, start, end, status, operatorId
            AttendanceRecord newRecord = new AttendanceRecord(
                    String.valueOf(System.currentTimeMillis()) + String.valueOf(userId),
                    userId,
                    System.currentTimeMillis(),
                    null,
                    1,
                    userId
            );
            recordService.insert(newRecord);
        } else {
            //haven't sign in
            throw new ServiceException(CExceptionEnum.USER_ONLINE);
        }
        BeanUtils.copyProperties(rank, rankDTO);
        rankDTO.setUserName(user.getName());
        return rankDTO;
    }

    public RankDTO signOut(Long userId) {
        User user = getUserByUserId(userId);
        if (user == null) throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST);
        AttendanceRecord onlineRecord = recordService.getOnlineRecordByUserId(userId);
        AttendanceRank rank = rankService.selectByUserIdAndWeek(userId, TimeHelper.getNowWeek());
        //Judging if Online
        RankDTO rankDTO = new RankDTO();
        if (onlineRecord == null) {
            throw new ServiceException(CExceptionEnum.USER_OFFLINE);
        } else {
            onlineRecord.setStatus(0);
            onlineRecord.setEnd(System.currentTimeMillis());
            recordService.updateById(onlineRecord);
            rank.setTotalTime(rank.getTotalTime() + onlineRecord.getEnd() - onlineRecord.getStart());
            rankService.updateById(rank);
            rankDTO.setAccumulatedTime(onlineRecord.getEnd() - onlineRecord.getStart());
        }
        BeanUtils.copyProperties(rank, rankDTO);
        rankDTO.setUserName(user.getName());
        return rankDTO;
    }

    public Object complaint(Long targetUserId, Long operatorUserId) {
        //todo judge these userId
        User user = getUserByUserId(targetUserId);
        if (user == null) throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST);
        //todo Test
        if (operatorUserId == null) throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST);
        AttendanceRecord onlineRecord = recordService.getOnlineRecordByUserId(targetUserId);
        if (onlineRecord != null) {
            onlineRecord.setStatus(-1);
            onlineRecord.setEnd(System.currentTimeMillis());
            onlineRecord.setOperatorId(operatorUserId);
            recordService.updateById(onlineRecord);
            mailService.sendMailByUserId(targetUserId, "complaint.html", "<科协签到>下线通知");
        } else {
            throw new ServiceException(CExceptionEnum.USER_C_OFFLINE);
        }
        return null;
    }

    public void helpSignOut(Long userId) {
        AttendanceRecord onlineRecord = recordService.getOnlineRecordByUserId(userId);
        if (onlineRecord != null) {
            onlineRecord.setStatus(-1);
            onlineRecord.setEnd(System.currentTimeMillis());
            onlineRecord.setOperatorId(5201314L);
            recordService.updateById(onlineRecord);
        }
    }

    public User getUserByUserId(Long userId) {
        return userMapper.selectByUserId(userId);
    }
}
