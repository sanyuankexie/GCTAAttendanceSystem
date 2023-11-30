package org.sanyuankexie.attendance.model.thread;

import lombok.AllArgsConstructor;
import org.sanyuankexie.attendance.service.MailService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@AllArgsConstructor
public class EmailThread implements Runnable{
    private Long userId;
    private String mailTemplateName;
    private String titleName;
    private MailService mailService;

    @Override
    public void run() {
        mailService.sendMailByUserId(userId, mailTemplateName, titleName);
    }
}
