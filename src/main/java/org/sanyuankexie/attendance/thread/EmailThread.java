package org.sanyuankexie.attendance.thread;

import lombok.AllArgsConstructor;
import org.sanyuankexie.attendance.model.AttachmentData;
import org.sanyuankexie.attendance.service.MailService;

import java.io.ByteArrayOutputStream;

@AllArgsConstructor
public class EmailThread implements Runnable{
    private MailService mailService;
    private Long userId;
    private String mailTemplateName;
    private String titleName;
    private AttachmentData attachmentData;

    @Override
    public void run() {
        mailService.sendMailByUserId(userId, mailTemplateName, titleName, attachmentData);
    }
}
