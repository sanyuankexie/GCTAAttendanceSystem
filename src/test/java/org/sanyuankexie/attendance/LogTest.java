package org.sanyuankexie.attendance;


import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LogTest {

    Logger logger= LoggerFactory.getLogger("org.sanyuankexie.attendance.advice.ExceptionControllerAdvice");

    @Test
    void logtest(){
        logger.info("test");
    }
}
