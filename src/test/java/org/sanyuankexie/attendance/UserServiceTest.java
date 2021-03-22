package org.sanyuankexie.attendance;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.sanyuankexie.attendance.common.DTO.RankDTO;
import org.sanyuankexie.attendance.mapper.AttendanceRankMapper;
import org.sanyuankexie.attendance.model.AttendanceRank;
import org.sanyuankexie.attendance.model.User;
import org.sanyuankexie.attendance.service.UserService;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class UserServiceTest {
    @Resource
    private UserService userService;


    //    @Test
//    public void sign() {
//        Object signInRank = userService.signIn(1900310227L);
//        System.out.println(JSONObject.toJSONString(signInRank));
//
//
//        Object signOutrank = userService.signOut(1900310227L);
//        System.out.println(JSONObject.toJSONString(signOutrank));
//    }
//
    @Test
    public void complaint() {
    }
}
