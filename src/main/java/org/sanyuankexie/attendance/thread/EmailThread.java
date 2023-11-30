package org.sanyuankexie.attendance.thread;

import lombok.AllArgsConstructor;
import org.sanyuankexie.attendance.service.MailService;

@AllArgsConstructor
public class EmailThread implements Runnable{
    private MailService mailService;
    private Long userId;
    private String mailTemplateName;
    private String titleName;

    @Override
    public void run() {
        mailService.sendMailByUserId(userId, mailTemplateName, titleName);
    }
}
