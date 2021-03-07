package org.sanyuankexie.attendance.service;

import org.sanyuankexie.attendance.common.DTO.RankDTO;
import org.sanyuankexie.attendance.common.DTO.RecordDTO;
import org.sanyuankexie.attendance.common.helper.TimeHelper;
import org.sanyuankexie.attendance.mapper.AttendanceRankMapper;
import org.sanyuankexie.attendance.mapper.AttendanceRecordMapper;
import org.sanyuankexie.attendance.model.AttendanceRank;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AttendanceRankService {
    @Resource
    private AttendanceRankMapper rankMapper;

    @Resource
    private AttendanceRecordMapper recordMapper;

    public List<RankDTO> getTopFive() {
//        return rankMapper.getTopFive(TimeHelper.getNowWeek());
        List<RankDTO> rankDTOList = rankMapper.getAll(TimeHelper.getNowWeek());
        if (rankDTOList == null) return null;
        List<RankDTO> resList = new ArrayList<>();
        AtomicInteger timesOf20 = new AtomicInteger(); //20级的人数
        for (RankDTO self : rankDTOList) {
            if (self.getUserId() / 100000000 == 20L) {
                timesOf20.getAndIncrement();
            }
            resList.add(self);
            if (timesOf20.get() >= 5 || resList.size() >= 10) break;
        }
        return resList;
    }

    public AttendanceRank selectByUserIdAndWeek(Long userId, int week) {
        return rankMapper.selectByUserIdAndWeek(userId, week);
    }

    public void insert(AttendanceRank rank) {
        rankMapper.insert(rank);
    }

    public void updateById(AttendanceRank rank) {
        rankMapper.updateById(rank);
    }

    public List<RecordDTO> getOnlineUserList() {
        return recordMapper.selectOnlineRecord();
    }
}
