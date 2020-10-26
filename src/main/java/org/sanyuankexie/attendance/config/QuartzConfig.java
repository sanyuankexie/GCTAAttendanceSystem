package org.sanyuankexie.attendance.config;

import org.quartz.*;
import org.sanyuankexie.attendance.common.job.AutoSignOutJob;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    private static final int TIME = 2; // 更新频率
    @Bean
    public JobDetail signOutJobDetail(){
        return JobBuilder.newJob(AutoSignOutJob.class).withIdentity("signOutJob").storeDurably().build();
    }

    @Bean
    public Trigger trigger1(){
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0 30 23 * * ? *");

        return TriggerBuilder.newTrigger()
                .forJob(signOutJobDetail())
                .withIdentity("signOutJobTrigger")
                .withSchedule(cronScheduleBuilder)
                .build();
    }
}
