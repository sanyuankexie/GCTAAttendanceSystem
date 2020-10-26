package org.sanyuankexie.attendance.service;

import org.sanyuankexie.attendance.common.DTO.RankDTO;
import org.sanyuankexie.attendance.common.DTO.RecordDTO;
import org.sanyuankexie.attendance.common.api.ResultVO;
import org.sanyuankexie.attendance.common.helper.ResultHelper;
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

    public ResultVO<RankDTO> signIn(Long userId) {
        User user = getUserByUserId(userId);
        if (user == null) return ResultHelper.error(-1, "学号不存在");
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
            return ResultHelper.error(-1, "不许重复签到");
        }
        BeanUtils.copyProperties(rank, rankDTO);
        rankDTO.setUserName(user.getUserName());
        return ResultHelper.success(rankDTO, "签到成功");
    }

    public ResultVO<RankDTO> signOut(Long userId) {
        User user = getUserByUserId(userId);
        if (user == null) return ResultHelper.error(-1, "学号不存在");
        AttendanceRecord onlineRecord = recordService.getOnlineRecordByUserId(userId);
        AttendanceRank rank = rankService.selectByUserIdAndWeek(userId, TimeHelper.getNowWeek());
        //Judging if Online
        RankDTO rankDTO = new RankDTO();
        if (onlineRecord == null) {
            return ResultHelper.error(-1, "没有签到");
        } else {
            onlineRecord.setStatus(0);
            onlineRecord.setEnd(System.currentTimeMillis());
            recordService.updateById(onlineRecord);
            rank.setTotalTime(rank.getTotalTime() + onlineRecord.getEnd() - onlineRecord.getStart());
            rankService.updateById(rank);
            rankDTO.setAccumulatedTime(onlineRecord.getEnd() - onlineRecord.getStart());
        }
        BeanUtils.copyProperties(rank, rankDTO);
        rankDTO.setUserName(user.getUserName());
        return ResultHelper.success(rankDTO, "签退成功");
    }

    public ResultVO<Object> complaint(Long targetUserId, Long operatorUserId) {
        //todo judge these userId
        User user = getUserByUserId(targetUserId);
        if (user == null) return ResultHelper.error(-1, "学号不存在");
        if (operatorUserId == null) operatorUserId = 1900310227L;
        AttendanceRecord onlineRecord = recordService.getOnlineRecordByUserId(targetUserId);
        if (onlineRecord != null) {
            onlineRecord.setStatus(-1);
            onlineRecord.setEnd(System.currentTimeMillis());
            onlineRecord.setOperatorId(operatorUserId);
            recordService.updateById(onlineRecord);
            mailService.sendMailByUserId(targetUserId, "complaint.html", "测试：<科协签到>下线通知");
        }
        return ResultHelper.success(null, "举报成功");
    }

    public void helpSignOut(Long userId) {
        AttendanceRecord onlineRecord = recordService.getOnlineRecordByUserId(userId);
        if (onlineRecord != null) {
            onlineRecord.setStatus(-1);
            onlineRecord.setEnd(System.currentTimeMillis());
            onlineRecord.setOperatorId(10000L);
            recordService.updateById(onlineRecord);
        }
    }

    public User getUserByUserId(Long userId) {
        return userMapper.selectByUserId(userId);
    }
}
