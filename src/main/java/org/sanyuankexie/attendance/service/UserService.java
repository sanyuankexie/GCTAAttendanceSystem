package org.sanyuankexie.attendance.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.sanyuankexie.attendance.advice.ExceptionControllerAdvice;
import org.sanyuankexie.attendance.common.DTO.RankDTO;
import org.sanyuankexie.attendance.common.exception.CExceptionEnum;
import org.sanyuankexie.attendance.common.exception.ServiceException;
import org.sanyuankexie.attendance.common.helper.TimeHelper;
import org.sanyuankexie.attendance.mapper.AttendanceRankMapper;
import org.sanyuankexie.attendance.mapper.UserInsertMapper;
import org.sanyuankexie.attendance.mapper.UserMapper;
import org.sanyuankexie.attendance.model.AttendanceRank;
import org.sanyuankexie.attendance.model.AttendanceRecord;
import org.sanyuankexie.attendance.model.SystemInfo;
import org.sanyuankexie.attendance.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Service
public class UserService {
    private static final ConcurrentHashMap<Long, Long> defenderMap = new ConcurrentHashMap<>();

    @Resource
    private MailService mailService;

    @Resource
    private AttendanceRankMapper rankMapper;

    @Resource
    private AttendanceRankService rankService;

    @Resource
    private AttendanceRecordService recordService;

    @Resource
    private UserMapper userMapper;

    private final SystemInfo systemInfo;

    private final TimeHelper timeHelper;

    public UserService(TimeHelper timeHelper, SystemInfo systemInfo) {
        this.timeHelper = timeHelper;
        this.systemInfo = systemInfo;
    }


    @Transactional
    public RankDTO signIn(Long userId) {
        Long now = System.currentTimeMillis();
        if(timeHelper.noAllSign(now)){
            throw new ServiceException(CExceptionEnum.No_ALLOW_TIME, userId);
        }
        User user = getUserByUserId(userId);

        if (user == null) throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST, userId);
        AttendanceRecord onlineRecord = recordService.getOnlineRecordByUserId(userId);
        AttendanceRank rank = rankService.selectByUserIdAndWeek(userId, timeHelper.getNowWeek());

        //defender start

        if (defenderMap.get(userId) == null) {
            defenderMap.put(userId, System.currentTimeMillis());
        } else {
            if (now - defenderMap.get(userId) <= 1000 * 15) {
                throw new ServiceException(CExceptionEnum.FREQUENT_OPERATION, userId);
            }
        }
        defenderMap.put(userId, now);
        //defender end

        // If is first sign in
        if (rank == null) {
            //id, userId, week, totalTime
            rank = new AttendanceRank(
                    1,
                     userId,
                    timeHelper.getNowWeek(),
                    0L,systemInfo.getTerm()

            );
            rank.setId(null);
            rankService.insert(rank);
        }
        //Judging if Online
        RankDTO rankDTO = new RankDTO();
        if (onlineRecord == null) {
            //id, userId, start, end, status, operatorId
            AttendanceRecord newRecord = new AttendanceRecord(
                    String.valueOf(System.currentTimeMillis()) + String.valueOf(userId),
                    userId,
                    System.currentTimeMillis(),
                    null,
                    1,
                    userId,systemInfo.getTerm()
            );
            recordService.insert(newRecord);
        } else {
            //haven't sign in
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

        //Judging if Online
        RankDTO rankDTO = new RankDTO();
        if (onlineRecord == null) {
            throw new ServiceException(CExceptionEnum.USER_OFFLINE, userId);
        } else {
            onlineRecord.setStatus(0);
            onlineRecord.setEnd(System.currentTimeMillis());
            recordService.updateById(onlineRecord);
            rank.setTotalTime(rank.getTotalTime() + onlineRecord.getEnd() - onlineRecord.getStart());
            rankService.updateById(rank);
            rankDTO.setAccumulatedTime(onlineRecord.getEnd() - onlineRecord.getStart());
        }
        BeanUtils.copyProperties(rank, rankDTO);
        rankDTO.setUserName(user.getName());
        return rankDTO;
    }

    public Object complaint(Long targetUserId, Long operatorUserId) {
        //todo judge these userId
        User user = getUserByUserId(targetUserId);
        if (user == null) throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST, targetUserId);
        //todo Test
        if (operatorUserId == null) throw new ServiceException(CExceptionEnum.USER_ID_NO_EXIST, operatorUserId);
        AttendanceRecord onlineRecord = recordService.getOnlineRecordByUserId(targetUserId);
        if (onlineRecord != null) {
            onlineRecord.setStatus(-1);
            onlineRecord.setEnd(System.currentTimeMillis());
            onlineRecord.setOperatorId(operatorUserId);
            recordService.updateById(onlineRecord);
            mailService.sendMailByUserId(targetUserId, "complaint.html", "[科协签到]: 举报下线通知");
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
    public RankDTO modifyTime(String operation, Long userId, String time, String token) {
        int week = timeHelper.getNowWeek();
        if (!token.equals(systemInfo.getPassword())) return null;
        if (operation.equals("add")) {

            Long res = (long) (Double.parseDouble(time)* 60 * 60 * 1000);
            rankMapper.add(userId, week, res);
            RankDTO rankDTO = new RankDTO();
            BeanUtils.copyProperties(rankService.selectByUserIdAndWeek(userId, week), rankDTO);
            return rankDTO;
        }
        if (operation.equals("sub")) {

        }
        throw new ServiceException(CExceptionEnum.UNKNOWN, userId);
//        return null;
    }

    public Map<String,Object> importUser(MultipartFile file,String password){
        Map<String,Object> map=new HashMap<>();
        if (!systemInfo.getPassword().equals(password)){
            map.put("result","密码不正确");

            return  map;
        }
        dataDao(file,map);
        return  map;



    }



    @Autowired
    UserInsertMapper insertMapper;

    public void dataDao(MultipartFile file,Map<String,Object> map) {
        final Integer[] sum = {0};
        final Integer[] in={0};
        Integer[] up={0};
        List<String> userInfo=new ArrayList<>();
        try {
            EasyExcel.read(file.getInputStream(), User.class, new PageReadListener<User>(dataList -> {
                for (User demoData : dataList) {
                    sum[0]++;

                    if (isNull(demoData)){
                        info(userInfo,demoData,"用户数据不全(除github)");
                        continue;
                    }

                    if (!checkMail(demoData.getEmail())){
                        info(userInfo,demoData,"邮箱检验不通过");
                        continue;
                    }

                    if ( String.valueOf(demoData.getId()).length()!="2000300223".length()){
                        info(userInfo,demoData,"邮箱长度不正确");
                        continue;

                    }
                    String[] location={"5109","5102","5108"};
                    if(Arrays.stream(location).noneMatch(v-> v.equals(demoData.getLocation()))) {
                        info(userInfo,demoData,"所在位置存在问题");
                        continue;
                    }
                    String[] dept={"多媒体部","软件部","硬件部","老人"};
                    if(Arrays.stream(dept).noneMatch(v-> v.equals(demoData.getDept()))) {
                        info(userInfo,demoData,"所在部门存在问题");
                        continue;
                    }

                    QueryWrapper<User> userQueryWrapper=new QueryWrapper<>();

                    userQueryWrapper.eq("id",demoData.getId());
                    User user = insertMapper.selectOne(userQueryWrapper);

                    if (user!=null){
                        List<String> change = getChange(user, demoData);
                        if (change.size()!=0){
                            insertMapper.updateById(demoData);
                            info(userInfo,user,"更新了数据("+ change+")");
                            up[0]++;
                        }
                    }else{
                        insertMapper.insert(demoData);
                        in[0]++;
                    }
                }
            })).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
            map.put("result","传入异常");
        }
        map.put("info",userInfo);
        map.put("result","总共人数:"+sum[0]+" 修改人数:"+up[0]+" 新增人数:"+in[0]);
    }

    public  void info(List<String> list,User user,String msg){
        list.add(user.getId() + "(" + user.getName() + ")" + ":" + msg);
    }
    public  boolean isNull(User user){

        return  user.getId()==null||user.getDept()==null||user.getLocation()==null||user.getName()==null||user.getEmail()==null;

    }

    public boolean checkMail(String email){
        String regEx="^[A-Za-z\\d]+([-_.][A-Za-z\\d]+)*@([A-Za-z\\d]+[-.])+[A-Za-z\\d]{2,4}$";
        return Pattern.matches(regEx,email);
    }

    public List<String> getChange(User oU,User nU){
        List<String> info=new ArrayList<>();
        Field[] declaredFields = User.class.getDeclaredFields();
        for(Field f:declaredFields){
            f.setAccessible(true);
            try {
                if (!String.valueOf(f.get(oU)).equals(String.valueOf(f.get(nU)))){
                    if(f.getName().equals("githubId")){
                        if(f.get(nU)==null){
                            continue;
                        }
                    }
                    info.add(f.get(oU)+"->"+f.get(nU));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return info;
    }
}
