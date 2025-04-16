package org.sanyuankexie.attendance.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.sanyuankexie.attendance.common.DTO.RankDTO;
import org.sanyuankexie.attendance.common.DTO.RecordDTO;
import org.sanyuankexie.attendance.common.DTO.UserStatusEnum;
import org.sanyuankexie.attendance.common.exception.CExceptionEnum;
import org.sanyuankexie.attendance.common.exception.ServiceException;
import org.sanyuankexie.attendance.common.helper.TimeHelper;
import org.sanyuankexie.attendance.mapper.AttendanceRankMapper;
import org.sanyuankexie.attendance.mapper.AttendanceRecordMapper;
import org.sanyuankexie.attendance.mapper.UserInsertMapper;
import org.sanyuankexie.attendance.mapper.UserMapper;
import org.sanyuankexie.attendance.model.AttendanceRank;
import org.sanyuankexie.attendance.model.AttendanceRecord;
import org.sanyuankexie.attendance.model.SystemInfo;
import org.sanyuankexie.attendance.model.User;
import org.sanyuankexie.attendance.thread.EmailThread;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Service
@Slf4j
public class UserService {
    private static final ConcurrentHashMap<Long, Long> defenderMap = new ConcurrentHashMap<>();

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    private MailService mailService;

    @Resource
    private AttendanceRankMapper rankMapper;

    @Resource
    private AttendanceRankService rankService;

    @Resource
    private AttendanceRecordService recordService;

    @Autowired
    private AttendanceRecordMapper recordMapper;

    @Resource
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserInsertMapper insertMapper;

    private final TimeHelper timeHelper;
    private final SystemInfo systemInfo;

    public UserService(TimeHelper timeHelper, SystemInfo systemInfo) {
        this.timeHelper = timeHelper;
        this.systemInfo = systemInfo;
    }

    @Transactional
    public RankDTO signIn(Long userId) {
        Long now = System.currentTimeMillis();
        if (timeHelper.noAllSign(now)) {
            throw new ServiceException(CExceptionEnum.No_ALLOW_TIME, userId);
        }
        if (timeHelper.noStart(now)) {
            throw new ServiceException(CExceptionEnum.TERM_NO_START, userId);
        }
        User user = getUserByUserId(userId);

        if (user == null) throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST, userId);
        AttendanceRecord onlineRecord = recordService.getOnlineRecordByUserId(userId);
        AttendanceRank rank = rankService.selectByUserIdAndWeek(userId, timeHelper.getNowWeek());

        // Defender start
        if (defenderMap.get(userId) == null) {
            defenderMap.put(userId, System.currentTimeMillis());
        } else {
            if (now - defenderMap.get(userId) <= 1000 * 15) {
                throw new ServiceException(CExceptionEnum.FREQUENT_OPERATION, userId);
            }
        }
        defenderMap.put(userId, now);
        // Defender end

        // If first sign in
        if (rank == null) {
            rank = new AttendanceRank(
                    null,
                    userId,
                    timeHelper.getNowWeek(),
                    0L,
                    systemInfo.getTerm()
            );
            rankService.insert(rank);
        }

        // Judging if online
        RankDTO rankDTO = new RankDTO();
        if (onlineRecord == null) {
            AttendanceRecord newRecord = new AttendanceRecord(
                    System.currentTimeMillis() + "" + userId,
                    userId,
                    System.currentTimeMillis(),
                    null,
                    1,
                    userId,
                    systemInfo.getTerm(),
                    null
            );
            recordService.insert(newRecord);
        } else {
            throw new ServiceException(CExceptionEnum.USER_ONLINE, userId);
        }
        BeanUtils.copyProperties(rank, rankDTO);
        rankDTO.setUserName(user.getName());
        return rankDTO;
    }

    @Transactional
    public RankDTO signOut(Long userId) {
        User user = getUserByUserId(userId);
        if (user == null) throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST, userId);
        AttendanceRecord onlineRecord = recordService.getOnlineRecordByUserId(userId);
        AttendanceRank rank = rankService.selectByUserIdAndWeek(userId, timeHelper.getNowWeek());

        if (onlineRecord == null) {
            throw new ServiceException(CExceptionEnum.USER_OFFLINE, userId);
        } else {
            onlineRecord.setStatus(0);
            onlineRecord.setEnd(System.currentTimeMillis());
            recordService.updateById(onlineRecord);
            rank.setTotalTime(rank.getTotalTime() + onlineRecord.getEnd() - onlineRecord.getStart());
            rankService.updateById(rank);
            RankDTO rankDTO = new RankDTO();
            rankDTO.setAccumulatedTime(onlineRecord.getEnd() - onlineRecord.getStart());
            BeanUtils.copyProperties(rank, rankDTO);
            rankDTO.setUserName(user.getName());
            return rankDTO;
        }
    }

    public Object complaint(Long targetUserId, Long operatorUserId) {
        User user = getUserByUserId(targetUserId);
        if (user == null) throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST, targetUserId);
        if (operatorUserId == null) throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST, operatorUserId);
        AttendanceRecord onlineRecord = recordService.getOnlineRecordByUserId(targetUserId);
        if (onlineRecord != null) {
            onlineRecord.setStatus(-1);
            onlineRecord.setEnd(System.currentTimeMillis());
            onlineRecord.setOperatorId(operatorUserId);
            recordService.updateById(onlineRecord);
            threadPoolTaskExecutor.execute(new EmailThread(mailService, targetUserId, "complaint.html", "[科协签到]: 举报下线通知", null));
        } else {
            throw new ServiceException(CExceptionEnum.USER_C_OFFLINE, targetUserId);
        }
        return null;
    }

    public void helpSignOut(Long userId) {
        AttendanceRecord onlineRecord = recordService.getOnlineRecordByUserId(userId);
        if (onlineRecord != null) {
            onlineRecord.setStatus(-1);
            onlineRecord.setEnd(System.currentTimeMillis());
            onlineRecord.setOperatorId(5201314L);
            recordService.updateById(onlineRecord);
        }
    }

    public User getUserByUserId(Long userId) {
        return userMapper.selectByUserId(userId);
    }

    @Transactional
    public RankDTO modifyTime(String operation, Long userId, String time, String token, Integer week) {
        if (week == null) {
            week = timeHelper.getNowWeek();
        }
        if (!token.equals(systemInfo.getPassword())) {
            throw new ServiceException(CExceptionEnum.PASSWORD_INCORRECT);
        }
        int status;
        long res = (long) (Double.parseDouble(time) * 60 * 60 * 1000);
        switch (operation) {
            case "add":
                rankMapper.add(userId, week, res);
                status = UserStatusEnum.SYSTEM_GIVEN.getStatus();
                break;
            case "sub":
                rankMapper.sub(userId, week, res);
                status = UserStatusEnum.SYSTEM_TAKEN.getStatus();
                break;
            case "set": // 忘记签到的人太多辣，直接设成18h X)
                rankMapper.set(userId, week, res);
                status = UserStatusEnum.SYSTEM_GIVEN.getStatus();
                break;
            default:
                throw new ServiceException(CExceptionEnum.UNKNOWN, userId);
        }
        RankDTO rankDTO = new RankDTO();
        AttendanceRank attendanceRank = rankService.selectByUserIdAndWeek(userId, week);
        if (attendanceRank != null) {
            BeanUtils.copyProperties(attendanceRank, rankDTO);
        } else {
            AttendanceRank iRank = new AttendanceRank(
                    null,
                    userId,
                    timeHelper.getNowWeek(),
                    res,
                    systemInfo.getTerm()
            );
            rankService.insert(iRank);
        }
        long l = System.currentTimeMillis();
        AttendanceRecord attendanceRecord = new AttendanceRecord(l + "" + userId, userId, l, null, status,
                5201314L, systemInfo.getTerm(), res);
        recordMapper.insert(attendanceRecord);
        return rankDTO;
    }

    public Map<String, Object> importUser(MultipartFile file, String password) {
        Map<String, Object> map = new HashMap<>();
        if (!systemInfo.getPassword().equals(password)) {
            map.put("result", "密码不正确");
            return map;
        }
        dataDao(file, map);
        return map;
    }

    public Map<String, Object> deleteUser(String ids, String password) {
        Map<String, Object> map = new HashMap<>();
        if (!systemInfo.getPassword().equals(password)) {
            map.put("result", "密码不正确");
            return map;
        }

        List<Long> idList = new ArrayList<>();
        for (String id : ids.split(",")) {
            try {
                idList.add(Long.parseLong(id.trim()));
            } catch (NumberFormatException e) {
                // Invalid ID, skip it
            }
        }

        if (idList.isEmpty()) {
            map.put("result", "无效的用户ID");
            return map;
        }

        try {
            int deleteCount = userMapper.delete(idList);
            map.put("result", "成功删除 " + deleteCount + " 名用户");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("result", "删除过程中出现错误: " + e.getMessage());
        }

        return map;
    }

    public void exportUsersToCsv(HttpServletResponse response, String password, String grade) throws IOException {
        if (!systemInfo.getPassword().equals(password)) {
            response.setHeader("Content-Type", "application/json");
            Map<String, Object> map = new HashMap<>();
            map.put("code", CExceptionEnum.PASSWORD_INCORRECT.getCode());
            map.put("msg", CExceptionEnum.PASSWORD_INCORRECT.getMsg());
            objectMapper.writeValue(response.getOutputStream(), map);
            return;
        }

        String filename = "users.csv";
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setHeader("Content-Type", "text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setHeader("Content-Type", "application/json; charset=UTF-8");

        try (PrintWriter writer = response.getWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            String[] header = {"ID", "Name", "Dept", "Location", "Email", "Github ID", "Grade", "Learn"};
            csvWriter.writeNext(header);

            List<User> users = userMapper.selectList(grade);
            for (User user : users) {
                String[] data = {
                        String.valueOf(user.getId()),
                        user.getName(),
                        user.getDept(),
                        user.getLocation(),
                        user.getEmail(),
                        user.getGithubId(),
                        user.getGrade(),
                        user.getLearn()
                };
                csvWriter.writeNext(data);
            }
        } catch (IOException e) {
            response.setHeader("Content-Type", "application/json");
            Map<String, Object> map = new HashMap<>();
            map.put("code", CExceptionEnum.SERVER_INTERNAL_ERROR.getCode());
            map.put("msg", CExceptionEnum.SERVER_INTERNAL_ERROR.getMsg());
            objectMapper.writeValue(response.getOutputStream(), map);
            e.printStackTrace();
        }
    }

    public void dataDao(MultipartFile file, Map<String, Object> map) {
        final int[] sum = {0};
        final int[] inCount = {0};
        final int[] up = {0};
        List<String> userInfo = new ArrayList<>();
        try {
            EasyExcel.read(file.getInputStream(), User.class, new PageReadListener<User>(dataList -> {
                for (User demoData : dataList) {
                    sum[0]++;

                    if (isNull(demoData)) {
                        info(userInfo, demoData, "用户数据不全(除github)");
                        continue;
                    }

                    if (!checkMail(demoData.getEmail())) {
                        info(userInfo, demoData, "邮箱检验不通过");
                        continue;
                    }

                    if (String.valueOf(demoData.getId()).length() != "2000300223".length()) {
                        info(userInfo, demoData, "邮箱长度不正确");
                        continue;
                    }

                    String[] location = {"5109", "5111", "5108"};
                    if (Arrays.stream(location).noneMatch(v -> v.equals(demoData.getLocation()))) {
                        info(userInfo, demoData, "所在位置存在问题");
                        continue;
                    }

                    String[] dept = {"多媒体部", "软件部", "硬件部", "老人"};
                    if (Arrays.stream(dept).noneMatch(v -> v.equals(demoData.getDept()))) {
                        info(userInfo, demoData, "所在部门存在问题");
                        continue;
                    }

                    QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                    userQueryWrapper.eq("id", demoData.getId());
                    User user = insertMapper.selectOne(userQueryWrapper);

                    if (user != null) {
                        List<String> change = getChange(user, demoData);
                        if (!change.isEmpty()) {
                            insertMapper.updateById(demoData);
                            info(userInfo, user, "更新了数据(" + change + ")");
                            up[0]++;
                        }
                    } else {
                        insertMapper.insert(demoData);
                        inCount[0]++;
                    }
                }
            })).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
            map.put("result", "传入异常");
        }
        map.put("info", userInfo);
        map.put("result", "总共人数:" + sum[0] + " 修改人数:" + up[0] + " 新增人数:" + inCount[0]);
    }

    public void info(List<String> list, User user, String msg) {
        list.add(user.getId() + "(" + user.getName() + ")" + ": " + msg);
    }

    public boolean isNull(User user) {
        return user.getId() == null || user.getDept() == null || user.getLocation() == null || user.getName() == null || user.getEmail() == null;
    }

    public boolean checkMail(String email) {
        String regEx = "^[A-Za-z\\d]+([-_.][A-Za-z\\d]+)*@([A-Za-z\\d]+[-.])+[A-Za-z\\d]{2,4}$";
        return Pattern.matches(regEx, email);
    }

    public List<String> getChange(User oU, User nU) {
        List<String> info = new ArrayList<>();
        Field[] declaredFields = User.class.getDeclaredFields();
        for (Field f : declaredFields) {
            f.setAccessible(true);
            try {
                if (!Objects.equals(f.get(oU), f.get(nU))) {
                    if ("githubId".equals(f.getName()) && f.get(nU) == null) {
                        continue;
                    }
                    info.add(f.get(oU) + "->" + f.get(nU));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return info;
    }

    public String encrypt(String text, String key) {
        byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

        // 循环异或加密
        byte[] encryptedBytes = new byte[textBytes.length];
        for (int i = 0; i < textBytes.length; i++) {
            encryptedBytes[i] = (byte) (textBytes[i] ^ keyBytes[i % keyBytes.length]);
        }

        // Base64编码
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    @Transactional
    public String login(Long id, String password) {
        User user = userMapper.selectByUserId(id);
        if (user != null && user.getRole() > 0 && systemInfo.getPassword().equals(password)) {
            return encrypt(id.toString(), "kexieisbest");
        } else {
            throw new ServiceException(CExceptionEnum.LOGIN_FAILED);
        }
    }


}
