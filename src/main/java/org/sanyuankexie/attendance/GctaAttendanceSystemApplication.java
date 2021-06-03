package org.sanyuankexie.attendance;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("org.sanyuankexie.attendance.mapper") //扫描mapper
@EnableScheduling   //开启计划任务
@EnableTransactionManagement  //开启sql事务
public class GctaAttendanceSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(GctaAttendanceSystemApplication.class, args);
    }

}
