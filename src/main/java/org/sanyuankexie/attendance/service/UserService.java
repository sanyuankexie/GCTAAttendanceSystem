package org.sanyuankexie.attendance.service;

import org.sanyuankexie.attendance.advice.ExceptionControllerAdvice;
import org.sanyuankexie.attendance.common.DTO.RankDTO;
import org.sanyuankexie.attendance.common.exception.CExceptionEnum;
import org.sanyuankexie.attendance.common.exception.ServiceException;
import org.sanyuankexie.attendance.common.helper.TimeHelper;
import org.sanyuankexie.attendance.mapper.AttendanceRankMapper;
import org.sanyuankexie.attendance.mapper.UserMapper;
import org.sanyuankexie.attendance.model.AttendanceRank;
import org.sanyuankexie.attendance.model.AttendanceRecord;
import org.sanyuankexie.attendance.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    private static final ConcurrentHashMap<Long, Long> defenderMap = new ConcurrentHashMap<>();

    @Value("${static.bassword}")
    private String bassword;

    @Resource
    private MailService mailService;

    @Resource
    private AttendanceRankMapper rankMapper;

    @Resource
    private AttendanceRankService rankService;

    @Resource
    private AttendanceRecordService recordService;

    @Resource
    private UserMapper userMapper;

    @Transactional
    public RankDTO signIn(Long userId) {
        User user = getUserByUserId(userId);
        if (user == null) throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST, userId);
        AttendanceRecord onlineRecord = recordService.getOnlineRecordByUserId(userId);
        AttendanceRank rank = rankService.selectByUserIdAndWeek(userId, TimeHelper.getNowWeek());

        //defender start
        Long now = System.currentTimeMillis();
        if (defenderMap.get(userId) == null) {
            defenderMap.put(userId, System.currentTimeMillis());
        } else {
            if (now - defenderMap.get(userId) <= 1000 * 15) {
                throw new ServiceException(CExceptionEnum.FREQUENT_OPERATION, userId);
            }
        }
        defenderMap.put(userId, now);
        //defender end

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
            throw new ServiceException(CExceptionEnum.USER_ONLINE, userId);
        }
        BeanUtils.copyProperties(rank, rankDTO);
        rankDTO.setUserName(user.getName());
        return rankDTO;
    }

    @Transactional
    public RankDTO signOut(Long userId) {
        User user = getUserByUserId(userId);
        if (user == null) throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST, userId);
        AttendanceRecord onlineRecord = recordService.getOnlineRecordByUserId(userId);
        AttendanceRank rank = rankService.selectByUserIdAndWeek(userId, TimeHelper.getNowWeek());
        //Judging if Online
        RankDTO rankDTO = new RankDTO();
        if (onlineRecord == null) {
            throw new ServiceException(CExceptionEnum.USER_OFFLINE, userId);
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
        if (user == null) throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST, targetUserId);
        //todo Test
        if (operatorUserId == null) throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST, operatorUserId);
        AttendanceRecord onlineRecord = recordService.getOnlineRecordByUserId(targetUserId);
        if (onlineRecord != null) {
            onlineRecord.setStatus(-1);
            onlineRecord.setEnd(System.currentTimeMillis());
            onlineRecord.setOperatorId(operatorUserId);
            recordService.updateById(onlineRecord);
            mailService.sendMailByUserId(targetUserId, "complaint.html", "[科协签到]: 举报下线通知");
        } else {
            throw new ServiceException(CExceptionEnum.USER_C_OFFLINE, targetUserId);
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

    @Transactional
    public RankDTO modifyTime(String operation, Long userId, String time, String token) {
        Integer week = TimeHelper.getNowWeek();
        if (!token.equals(bassword)) return null;
        if (operation.equals("add")) {
            Long res = Long.parseLong(time) * 60 * 60 * 1000;
            rankMapper.add(userId, week, res);
            RankDTO rankDTO = new RankDTO();
            BeanUtils.copyProperties(rankService.selectByUserIdAndWeek(userId, week), rankDTO);
            return rankDTO;
        }
        if (operation.equals("sub")) {

        }
        return null;
    }
}
