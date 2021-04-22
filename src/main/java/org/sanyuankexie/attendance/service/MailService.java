package org.sanyuankexie.attendance.service;


import com.therainisme.AmeBox.logUtil.LogFactory;
import com.therainisme.AmeBox.logUtil.Logger;
import org.sanyuankexie.attendance.model.User;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MailService {
    Logger logger = LogFactory.getLogger(this);

    @Resource
    private UserService userService;

    @Resource
    private TemplateEngine templateEngine;

    @Resource
    private JavaMailSender javaMailSender;


    public void sendMailByUserId(Long userId, String mailTemplateName, String title) {

        Context context = new Context();


        User user = userService.getUserByUserId(userId);
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return;
        }
        //这里是搜索用户信息然后替换的
        context.setVariable("userId", user.getId()); //学号
        context.setVariable("userName", user.getName()); // 姓名
        context.setVariable("nowDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));


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
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("科协官方" + "<official@kexie.space>"); // 这里换成科协的邮箱
            messageHelper.setTo(user.getEmail());
            messageHelper.setSubject(title);

            String mailContent = templateEngine.process(mailTemplateName, context);
            messageHelper.setText(mailContent, true);

            javaMailSender.send(messageHelper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error(false, "发送<" + userId + ">的邮件失败");
        }
    }
}


