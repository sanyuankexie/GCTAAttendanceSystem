package org.sanyuankexie.attendance.common.job;

import org.quartz.JobExecutionContext;
import org.sanyuankexie.attendance.common.DTO.RecordDTO;
import org.sanyuankexie.attendance.service.AttendanceRankService;
import org.sanyuankexie.attendance.service.MailService;
import org.sanyuankexie.attendance.service.UserService;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;
import java.util.List;

public class AutoSignOutJob extends QuartzJobBean {

    @Resource
    private UserService userService;

    @Resource
    private MailService mailService;

    @Resource
    private AttendanceRankService attendanceRankService;


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {

        List<RecordDTO> onlineUsers = attendanceRankService.getOnlineUserList();
        if (onlineUsers.isEmpty()) {
            System.out.println("当前无在线学生");
            return;
        }
        for (RecordDTO onlineUser : onlineUsers) {
            try {
                Long userId = onlineUser.getUserId();
                userService.helpSignOut(userId);
                mailService.sendMailByUserId(userId, "autoSignOut.html", "[科协签到]: 晚间签退通知");
                System.out.println("userID:" + userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
