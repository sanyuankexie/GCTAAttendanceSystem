package org.sanyuankexie.attendance.controller;

import com.alibaba.fastjson.JSONObject;
import org.sanyuankexie.attendance.common.DTO.RankDTO;
import org.sanyuankexie.attendance.common.api.ResultVO;
import org.sanyuankexie.attendance.common.helper.ResultHelper;
import org.sanyuankexie.attendance.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/api/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping(value = "/signIn")
    public ResultVO<RankDTO> signIn(@RequestBody JSONObject jsonObject) {
        Long userId = jsonObject.getLong("userId");
        return userService.signIn(userId);
    }

    @PostMapping(value = "/signOut")
    public ResultVO<RankDTO> signOut(@RequestBody JSONObject jsonObject) {
        Long userId = jsonObject.getLong("userId");
        return userService.signOut(userId);
    }

    @PostMapping(value = "/complaint")
    public Object complaint(@RequestBody JSONObject jsonObject) {
        return userService.complaint(jsonObject.getLong("targetUserId"), jsonObject.getLong("operatorUserId"));
    }
}
