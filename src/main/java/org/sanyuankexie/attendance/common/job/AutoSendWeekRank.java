package org.sanyuankexie.attendance.common.job;

import lombok.extern.slf4j.Slf4j;
import org.sanyuankexie.attendance.common.helper.TimeHelper;
import org.sanyuankexie.attendance.model.AttachmentData;
import org.sanyuankexie.attendance.model.SystemInfo;
import org.sanyuankexie.attendance.service.AttendanceRankService;
import org.sanyuankexie.attendance.service.MailService;
import org.sanyuankexie.attendance.thread.EmailThread;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.internet.MimeUtility;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class AutoSendWeekRank {

    @Resource
    @Lazy
    private MailService mailService;

    @Resource
    private AttendanceRankService attendanceRankService;

    @Resource
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    private TimeHelper timeHelper;

    @Resource
    private SystemInfo systemInfo;

    // 每周一早上 8.00 发送上周打卡排名邮件
    @Scheduled(cron = "0 0 8 ? * MON")
    void weeklySendRank(){
        try {
            scheduleSendMail();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void scheduleSendMail() throws IOException {
        int week = -1;
        String term = systemInfo.getTerm();
        int nowWeek = timeHelper.getNowWeek();
        String[] t = term.split("_");
        String lable = t[0] + "-" + t[1] + ("1".equals(t[2]) ? "上学期" : "下学期");
        ByteArrayOutputStream stream = attendanceRankService.generateLastWeekRankExcelBytes(term, week);
        String fileName =  lable + "第" + (nowWeek - 1) + "周" + ".xlsx";
        String encodedFileName = MimeUtility.encodeText(fileName, "UTF-8", "B");
        AttachmentData attachmentData = new AttachmentData();
        attachmentData.setFilename(encodedFileName);
        attachmentData.setStream(stream);
        try {
            Long target = systemInfo.getMailTarget();
            threadPoolTaskExecutor.execute(new EmailThread(mailService, target, "AutoSendRank.html", "[科协通知]: 每周打卡统计", attachmentData));
            log.info("<System><{}>已发送每周报表", target);
        } catch (Exception e) {
            log.error( "<System><{}>发送每周打卡排名失败");
        }
    }

}
