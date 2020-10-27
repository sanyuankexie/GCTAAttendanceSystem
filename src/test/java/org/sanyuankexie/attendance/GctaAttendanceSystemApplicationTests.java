package org.sanyuankexie.attendance;

import org.junit.jupiter.api.Test;
import org.sanyuankexie.attendance.common.api.ResultVO;
import org.sanyuankexie.attendance.common.helper.ClassHelper;
import org.sanyuankexie.attendance.model.User;
import org.sanyuankexie.attendance.service.AttendanceRankService;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
class GctaAttendanceSystemApplicationTests {
    @Resource
    private AttendanceRankService rankService;

    @Test
    void contextLoads() throws NoSuchFieldException, IllegalAccessException {
//        User user = new User();
//        user.setEmail("sb");
//        ResultVO<Object> resultVO = new ResultVO<>(user, 1, "1");
//        System.out.println(resultVO.getData() instanceof User);
//        System.out.println(ClassHelper.getObjectFieldValue(resultVO.getData(), "email"));
//        Object res = rankService.getTopFive();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(new Date(1603620726992L)));

    }

}
