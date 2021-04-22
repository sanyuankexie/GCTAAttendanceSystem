package org.sanyuankexie.attendance.common.job;

import com.therainisme.AmeBox.logUtil.LogFactory;
import com.therainisme.AmeBox.logUtil.Logger;
import org.quartz.JobExecutionContext;
import org.sanyuankexie.attendance.common.DTO.RecordDTO;
import org.sanyuankexie.attendance.service.AttendanceRankService;
import org.sanyuankexie.attendance.service.MailService;
import org.sanyuankexie.attendance.service.UserService;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

public class AutoSignOutJob extends QuartzJobBean {
    Logger logger = LogFactory.getLogger(this);

    @Resource
    private UserService userService;

    @Resource
    private MailService mailService;

    @Resource
    private AttendanceRankService attendanceRankService;


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        logger.log(true, "<System>开始自动签退");

        List<RecordDTO> onlineUsers = attendanceRankService.getOnlineUserList();
        if (onlineUsers.isEmpty()) {
            logger.log(true, "<System>当前无在线学生");
            return;
        }
        Long target = 5201314L;
        for (RecordDTO onlineUser : onlineUsers) {
            try {
                target = onlineUser.getUserId();
                userService.helpSignOut(target);
                mailService.sendMailByUserId(target, "AutoSignOut.html", "[科协签到]: 晚间签退通知");
                logger.log(true, "<System><" + target + ">已自动签退");
            } catch (Exception e) {
                logger.log(false, "<System><" + target + ">自动签退时发生了一些错误");
            }
        }
        logger.log("<System>当前剩余人数" + attendanceRankService.getOnlineUserList().size());
    }
}
