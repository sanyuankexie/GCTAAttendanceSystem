package org.sanyuankexie.attendance.config;


import lombok.extern.slf4j.Slf4j;
import org.sanyuankexie.attendance.common.helper.TimeHelper;
import org.sanyuankexie.attendance.model.SystemInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
@Slf4j
public class BeanConfig {


    private final SystemInfo systemInfo;

    public BeanConfig(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Bean
    public TimeHelper getTimerHelper(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date w0 = simpleDateFormat.parse(systemInfo.getWeek());

            return new TimeHelper(w0.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        log.info("学期第一天获取失败，请检查配置文件是否配置attendance.week变量");
        return new TimeHelper(1614556800000L); //2021 3 1
    }


}
