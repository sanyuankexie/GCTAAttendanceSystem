package org.sanyuankexie.attendance;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.sanyuankexie.attendance.common.DTO.RankDTO;
import org.sanyuankexie.attendance.mapper.AttendanceRankMapper;
import org.sanyuankexie.attendance.model.AttendanceRank;
import org.sanyuankexie.attendance.model.User;
import org.sanyuankexie.attendance.service.MailService;
import org.sanyuankexie.attendance.service.UserService;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class UserServiceTest {
    @Resource
    private UserService userService;

    @Resource
    private MailService mailService;

    @Test
    public void sign() {
//        mailService.sendMailByUserId(1900310227L, "complaint.html", "完蛋了你");
    }

    @Test
    public void complaint() {
    }
}
