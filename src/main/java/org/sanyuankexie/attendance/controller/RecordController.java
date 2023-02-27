package org.sanyuankexie.attendance.controller;

import org.apache.ibatis.annotations.Param;
import org.sanyuankexie.attendance.common.DTO.RankDTO;
import org.sanyuankexie.attendance.common.DTO.RecordDTO;
import org.sanyuankexie.attendance.common.DTO.UserStatusDTO;
import org.sanyuankexie.attendance.common.api.ResultVO;
import org.sanyuankexie.attendance.common.aspect.annotation.ConvertTime;
import org.sanyuankexie.attendance.common.helper.ResultHelper;
import org.sanyuankexie.attendance.service.AttendanceRankService;
import org.sanyuankexie.attendance.service.AttendanceRecordService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @GetMapping("/{userId}/{term}")
    public ResultVO<List<RecordDTO>> recordTerm(@PathVariable Long userId,@PathVariable String term){
        return ResultHelper.success(recordService.selectRecordListByUserId(userId,term), "成功获取个人数据");
    }

    @ConvertTime
    @GetMapping("/topFive")
    public ResultVO<List<RankDTO>> getTopFive(@RequestParam(value = "old-man", defaultValue = "") String oldMan) {
        if (oldMan == null || oldMan.equals("")) {
            return ResultHelper.success(rankService.getTopFive(), "成功获取有效排行榜");
        }else {
            return ResultHelper.success(rankService.getTopFiveOfOldMan(), "成功获取老人有效排行榜");
        }

    }

    @GetMapping("/online")
    public ResultVO<List<RecordDTO>> getIndex() {
        return ResultHelper.success(rankService.getOnlineUserList(), "成功获取在线用户列表");
    }

    @ConvertTime
    @GetMapping("/online/{userId}")
    public ResultVO<UserStatusDTO> isOnline(@PathVariable Long userId) {
        return ResultHelper.success(recordService.isOnlineByUserId(userId), "成功获取用户在线状态");
    }
    @GetMapping("/time/{userId}")
    public ResultVO<RecordDTO> getTime(@PathVariable Long userId){
        return ResultHelper.success(recordService.getUserStatus(userId), "成功获取用户在线状态");
    }

    //返回记录分组信息

    @GetMapping("/term/{userId}")
    public ResultVO<List<String>> getTerm(@PathVariable Long userId){
        return ResultHelper.success(recordService.getTerm(userId));
    }

    @GetMapping("/export")
    public void exportRank(@Param("password") String password, @Param("term") String term, @Param("week") Integer week, HttpServletResponse resp) throws IOException {
        rankService.dowRank(password,term,week,resp);
    }


}
