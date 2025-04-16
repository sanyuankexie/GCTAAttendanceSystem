package org.sanyuankexie.attendance.service;


import lombok.extern.slf4j.Slf4j;
import org.sanyuankexie.attendance.model.AttachmentData;
import org.sanyuankexie.attendance.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class MailService {
//    Logger logger = LogFactory.getLogger(this);

    private final UserService userService;

    private final TemplateEngine templateEngine;

    private final JavaMailSender javaMailSender;

    public MailService(JavaMailSender javaMailSender, UserService userService, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.userService = userService;
        this.templateEngine = templateEngine;
    }


    public void sendMailByUserId(Long userId, String mailTemplateName, String title, AttachmentData attachmentData) {

        Context context = new Context();


        User user = userService.getUserByUserId(userId);
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return;
        }
        //这里是搜索用户信息然后替换的
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        context.setVariable("userId", user.getId()); //学号
        context.setVariable("userName", user.getName()); // 姓名
        context.setVariable("nowDate", sdf.format(new Date()));




        String toMail = user.getEmail();

        // 邮箱验证规则
        String regEx = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(toMail);
        // 字符串是否与正则表达式相匹配
        boolean rs = matcher.matches();
        if (!rs) {
            return;
        }

        //下面是发送信息
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            messageHelper.setFrom("科协官方" + "<official@kexie.space>"); // 这里换成科协的邮箱
            messageHelper.setTo(user.getEmail());
            messageHelper.setSubject(title);

            String mailContent = templateEngine.process(mailTemplateName, context);
            messageHelper.setText(mailContent, true);
            // 添加附件（如果有）
            if (attachmentData != null) {
                messageHelper.addAttachment(attachmentData.getFilename(), new ByteArrayDataSource(attachmentData.getStream().toByteArray(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            }
            javaMailSender.send(messageHelper.getMimeMessage());
        } catch (MessagingException e) {
            log.error("发送<{}>的邮件失败",userId);
        }
    }
}


