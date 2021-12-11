

## 科协签到系统开发文档0.1.0



### 数据库建立

将下列sql在你的数据库执行

```sql
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";
CREATE TABLE `attendance_rank` (
  `id` int(11) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `week` int(11) NOT NULL,
  `total_time` bigint(20) NOT NULL,
  `term` varchar(15) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
CREATE TABLE `attendance_record` (
  `id` varchar(255) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `start` bigint(20) NOT NULL,
  `end` bigint(20) DEFAULT NULL,
  `status` int(11) NOT NULL,
  `operator_id` bigint(20) NOT NULL,
  `term` varchar(15) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
CREATE TABLE `book_shelf` (
  `book_name` varchar(255) NOT NULL,
  `version` tinyint(4) NOT NULL,
  `lang` enum('en_us','zh_cn') NOT NULL,
  `book_id` int(11) NOT NULL,
  `count` int(11) NOT NULL,
  `publisher` varchar(255) DEFAULT NULL,
  `available_count` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
CREATE TABLE `intervene_request` (
  `request_id` int(11) NOT NULL,
  `generator_id` bigint(20) NOT NULL,
  `content` varchar(255) DEFAULT NULL,
  `time_stamp` datetime DEFAULT NULL,
  `status` enum('pending','resolved','ignored') NOT NULL DEFAULT 'pending'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `dept` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `is_intervene` tinyint(1) NOT NULL DEFAULT '0',
  `github_id` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
ALTER TABLE `attendance_rank`
  ADD PRIMARY KEY (`id`);
ALTER TABLE `attendance_record`
  ADD PRIMARY KEY (`id`) USING BTREE;
ALTER TABLE `book_shelf`
  ADD PRIMARY KEY (`book_id`) USING BTREE;
ALTER TABLE `intervene_request`
  ADD PRIMARY KEY (`request_id`) USING BTREE;
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`) USING BTREE;
ALTER TABLE `attendance_rank`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `book_shelf`
  MODIFY `book_id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `intervene_request`
  MODIFY `request_id` int(11) NOT NULL AUTO_INCREMENT;
COMMIT;
```

### 信息配置

你可以在资源文件建立一个**application.properties**文件然后粘贴如下内容

```properties
#邮箱信息
spring.mail.host=smtp.exmail.qq.com
spring.mail.username=
spring.mail.password=
spring.mail.protocol=smtp
spring.mail.default-encoding=UTF-8
spring.mail.properties.mail.smtp.socketFactory.class=javax.net.ssl.SSLSocketFactory
spring.mail.properties.mail.smtp.socketFactory.port=465
#数据库信息，你懂的
spring.datasource.url: 
spring.datasource.username: 
spring.datasource.driver-class-name: com.mysql.cj.jdbc.Driver
spring.datasource.password: 
#后端通行密码
attendance.password='password'
#将小伙伴加入考核 
attendance.isLeve:[]
```



