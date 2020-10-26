package org.sanyuankexie.attendance.service;

import com.mysql.cj.util.TimeUtil;
import org.sanyuankexie.attendance.common.DTO.RankDTO;
import org.sanyuankexie.attendance.common.DTO.RecordDTO;
import org.sanyuankexie.attendance.common.api.ResultVO;
import org.sanyuankexie.attendance.common.helper.ResultHelper;
import org.sanyuankexie.attendance.common.helper.TimeHelper;
import org.sanyuankexie.attendance.mapper.AttendanceRankMapper;
import org.sanyuankexie.attendance.mapper.AttendanceRecordMapper;
import org.sanyuankexie.attendance.model.AttendanceRank;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AttendanceRankService {
    @Resource
    private AttendanceRankMapper rankMapper;

    @Resource
    private AttendanceRecordMapper recordMapper;

    public AttendanceRank selectByUserIdAndWeek(Long userId, int week) {
        return rankMapper.selectByUserIdAndWeek(userId, week);
    }

    public void insert(AttendanceRank rank) {
        rankMapper.insert(rank);
    }

    public void updateById(AttendanceRank rank) {
        rankMapper.updateById(rank);
    }

    public ResultVO<List<RankDTO>> getTopFiveResult() {
        return ResultHelper.success(rankMapper.getTopFive(TimeHelper.getNowWeek()), "成功获取排行榜前五");
    }

    public ResultVO<List<RecordDTO>> getOnlineUserListResult() {
        return ResultHelper.success(getOnlineUserList(), "成功获取在线用户列表");
    }

    public List<RecordDTO> getOnlineUserList() {
        return recordMapper.selectOnlineRecord();
    }
}
