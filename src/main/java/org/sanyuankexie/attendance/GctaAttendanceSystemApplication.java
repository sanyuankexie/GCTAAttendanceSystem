package org.sanyuankexie.attendance;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("org.sanyuankexie.attendance.mapper")
@EnableScheduling
@EnableTransactionManagement
public class GctaAttendanceSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(GctaAttendanceSystemApplication.class, args);
    }

}
