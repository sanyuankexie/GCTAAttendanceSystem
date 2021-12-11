package org.sanyuankexie.attendance;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.sanyuankexie.attendance.common.DTO.RankDTO;
import org.sanyuankexie.attendance.mapper.AttendanceRankMapper;
import org.sanyuankexie.attendance.model.AttendanceRank;
import org.sanyuankexie.attendance.model.User;
import org.sanyuankexie.attendance.service.MailService;
import org.sanyuankexie.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

//@SpringBootTest
public class UserServiceTest {
    @Resource
    private UserService userService;

    @Resource
    private MailService mailService;

    @Value("${spring.mail.host}")
    String u;
//    @Test
    public void sign() {
//        mailService.sendMailByUserId(2000300223L, "complaint.html", "完蛋了你");
//        System.out.println(u);
    }

    @Test
    public void complaint() {
        System.out.println(1/2);
    }
}
