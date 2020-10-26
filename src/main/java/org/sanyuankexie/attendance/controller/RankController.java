package org.sanyuankexie.attendance.controller;

import org.sanyuankexie.attendance.common.DTO.RankDTO;
import org.sanyuankexie.attendance.common.DTO.RecordDTO;
import org.sanyuankexie.attendance.common.api.ResultVO;
import org.sanyuankexie.attendance.service.AttendanceRankService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "/api/rank")
public class RankController {
    @Resource
    private AttendanceRankService rankService;

    @GetMapping("/topFive")
    public ResultVO<List<RankDTO>> getTopFive() {
        return rankService.getTopFiveResult();
    }

    @GetMapping("/")
    public ResultVO<List<RecordDTO>> getIndex() {
        return rankService.getOnlineUserListResult();
    }
}
