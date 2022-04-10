package org.sanyuankexie.attendance;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.sanyuankexie.attendance.common.helper.TimeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
@Slf4j
public class TimeTest {


    @Autowired
    TimeHelper timeHelper;


    @Test
    public  void t(){

        log.info("222");
//        System.out.println(timeHelper.noAllSign(new Date(1635026400010L).getTime()));

    }
}
