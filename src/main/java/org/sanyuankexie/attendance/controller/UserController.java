package org.sanyuankexie.attendance.controller;

import com.alibaba.fastjson.JSONObject;
import org.sanyuankexie.attendance.common.DTO.RankDTO;
import org.sanyuankexie.attendance.common.api.ResultVO;
import org.sanyuankexie.attendance.common.aspect.annotation.ConvertTime;
import org.sanyuankexie.attendance.common.helper.ResultHelper;
import org.sanyuankexie.attendance.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/api/user")
public class UserController {

    @Resource
    private UserService userService;


    @ConvertTime
    @PostMapping(value = "/signIn")
    public ResultVO<RankDTO> signIn(@RequestBody JSONObject jsonObject) {
        Long userId = jsonObject.getLong("userId");
        return ResultHelper.success(userService.signIn(userId), "签到成功");
    }

    @ConvertTime
    @PostMapping(value = "/signOut")
    public ResultVO<RankDTO> signOut(@RequestBody JSONObject jsonObject) {
        Long userId = jsonObject.getLong("userId");
        return ResultHelper.success(userService.signOut(userId), "签退成功");
    }

    @PostMapping(value = "/complaint")
    public Object complaint(@RequestBody JSONObject jsonObject) {
        return ResultHelper.success(userService.complaint(jsonObject.getLong("targetUserId"), jsonObject.getLong("operatorUserId")), "举报成功");
    }

    @ConvertTime
    @PostMapping(value = "/modify")
    public ResultVO<RankDTO> modifyTime(@RequestBody JSONObject jsonObject) {
        return ResultHelper.success(userService.modifyTime(
                jsonObject.getString("operation"),
                jsonObject.getLong("userId"),
                jsonObject.getString("time"),
                jsonObject.getString("token")));
    }
}
