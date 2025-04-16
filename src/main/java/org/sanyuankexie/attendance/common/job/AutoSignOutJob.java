package org.sanyuankexie.attendance.common.job;

import lombok.extern.slf4j.Slf4j;
import org.sanyuankexie.attendance.common.DTO.RecordDTO;
import org.sanyuankexie.attendance.service.AttendanceRankService;
import org.sanyuankexie.attendance.service.MailService;
import org.sanyuankexie.attendance.service.UserService;
import org.sanyuankexie.attendance.thread.EmailThread;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
@Slf4j
public class AutoSignOutJob {
//    Logger logger = LogFactory.getLogger(this);

    @Resource
    private UserService userService;

    @Resource
    private MailService mailService;

    @Resource
    private AttendanceRankService attendanceRankService;
    @Resource
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    //工作日
    @Scheduled(cron = "00 30 23 ? * 1-4,7")
    void work(){ executeInternal(); }
    //周五周六11.30不签退
    @Scheduled(cron = "00 00 00 ? * 6-7")
    void weekend(){
        executeInternal();
    }

    //工作日
    @Scheduled(cron = "00 29 23 ? * 1-4,7")
    void workingDayRemind(){ executeRemind(); }
    //周五周六11.30不签退
    @Scheduled(cron = "00 59 23 ? * 5-6")
    void weekendRemind(){
        executeRemind();
    }

    public void executeRemind() {
        log.info("<System>开始发邮件提醒");

        List<RecordDTO> onlineUsers = attendanceRankService.getOnlineUserList();
        if (onlineUsers.isEmpty()) {
            log.info("<System>当前无在线学生");
            return;
        }
        Long target = 5201314L;
        for (RecordDTO onlineUser : onlineUsers) {
            try {
                target = onlineUser.getUserId();
                threadPoolTaskExecutor.execute(new EmailThread(mailService, target, "AutoSignOutRemind.html", "[科协签到]: 今日签退提醒", null));
            } catch (Exception e) {
                log.error( "<System><{}>签退提醒发生了一些错误",target);
            }
        }
        log.info("<System>当前剩余人数:{}", attendanceRankService.getOnlineUserList().size());
    }

    public void executeInternal() {
        log.info("<System>开始自动签退");

        List<RecordDTO> onlineUsers = attendanceRankService.getOnlineUserList();
        if (onlineUsers.isEmpty()) {
            log.info("<System>当前无在线学生");
            return;
        }
        Long target = 5201314L;
        for (RecordDTO onlineUser : onlineUsers) {
            try {
                target = onlineUser.getUserId();
                userService.helpSignOut(target);
                threadPoolTaskExecutor.execute(new EmailThread(mailService, target, "AutoSignOut.html", "[科协签到]: 晚间签退通知", null));
                log.info("<System><{}>已自动签退", target );
            } catch (Exception e) {
                log.error( "<System><{}>自动签退时发生了一些错误",target);
            }
        }
        log.info("<System>当前剩余人数:{}",attendanceRankService.getOnlineUserList().size());
    }
}
