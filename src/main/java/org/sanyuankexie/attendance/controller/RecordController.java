package org.sanyuankexie.attendance.controller;

import org.sanyuankexie.attendance.common.DTO.RankDTO;
import org.sanyuankexie.attendance.common.DTO.RecordDTO;
import org.sanyuankexie.attendance.common.api.ResultVO;
import org.sanyuankexie.attendance.common.aspect.annotation.ConvertTime;
import org.sanyuankexie.attendance.common.helper.ResultHelper;
import org.sanyuankexie.attendance.mapper.AttendanceRecordMapper;
import org.sanyuankexie.attendance.service.AttendanceRankService;
import org.sanyuankexie.attendance.service.AttendanceRecordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "/api/record")
public class RecordController {
    @Resource
    private AttendanceRecordService recordService;

    @Resource
    private AttendanceRankService rankService;

    @GetMapping(value = "/{userId}")
    public ResultVO<List<RecordDTO>> record(@PathVariable Long userId){
        return ResultHelper.success(recordService.selectRecordListByUserId(userId), "成功获取个人数据");
    }

    @ConvertTime
    @GetMapping("/topFive")
    public ResultVO<List<RankDTO>> getTopFive() {
        return ResultHelper.success(rankService.getTopFive(), "成功获取有效排行榜");
    }

    @GetMapping("/online")
    public ResultVO<List<RecordDTO>> getIndex() {
        return ResultHelper.success(rankService.getOnlineUserList(), "成功获取在线用户列表");
    }

    @ConvertTime
    @GetMapping("/online/{userId}")
    public ResultVO<RecordDTO> isOnline(@PathVariable Long userId) {
        return ResultHelper.success(recordService.getUserStatus(userId), "成功获取用户在线状态");
    }
}
