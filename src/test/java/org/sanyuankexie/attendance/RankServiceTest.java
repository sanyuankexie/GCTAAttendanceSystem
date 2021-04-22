package org.sanyuankexie.attendance;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.sanyuankexie.attendance.service.AttendanceRankService;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class RankServiceTest {

    @Resource
    private AttendanceRankService rankService;

//    @Test
//    public void getTopFive(){
//        Object res = rankService.getTopFive();
//        System.out.println(JSONObject.toJSONString(res));
//    }
//
//    @Test
//    public void getOnlineUser(){
//        Object res = rankService.getOnlineUserList();
//        System.out.println(JSONObject.toJSONString(res));
//    }
}
