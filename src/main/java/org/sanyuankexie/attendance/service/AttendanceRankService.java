package org.sanyuankexie.attendance.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.sanyuankexie.attendance.common.DTO.RankDTO;
import org.sanyuankexie.attendance.common.DTO.RecordDTO;
import org.sanyuankexie.attendance.common.exception.CExceptionEnum;
import org.sanyuankexie.attendance.common.helper.TimeHelper;
import org.sanyuankexie.attendance.mapper.AttendanceRankMapper;
import org.sanyuankexie.attendance.mapper.AttendanceRecordMapper;
import org.sanyuankexie.attendance.model.AttendanceRank;
import org.sanyuankexie.attendance.model.RankExport;
import org.sanyuankexie.attendance.model.SystemInfo;
import org.sanyuankexie.attendance.model.TermRankExport;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class AttendanceRankService {
    private final AttendanceRankMapper rankMapper;

    private final AttendanceRecordMapper recordMapper;

    private final AttendanceRankMapper attendanceRankMapper;

    private final SystemInfo systemInfo;

    private final TimeHelper timeHelper;
    private final ObjectMapper objectMapper;

    public AttendanceRankService(TimeHelper timeHelper, SystemInfo systemInfo, AttendanceRankMapper rankMapper, AttendanceRecordMapper recordMapper, AttendanceRankMapper attendanceRankMapper, ObjectMapper objectMapper) {
        this.timeHelper = timeHelper;
        this.systemInfo = systemInfo;
        this.rankMapper = rankMapper;
        this.recordMapper = recordMapper;
        this.attendanceRankMapper = attendanceRankMapper;
        this.objectMapper = objectMapper;
    }

    public List<RankDTO> getTopFive() {
        List<RankDTO> rankDTOList = rankMapper.getTopFive(timeHelper.getNowWeek(), systemInfo.getTerm(),
                systemInfo.getGrade());
        if (rankDTOList == null) return null;
        List<RankDTO> resList = new ArrayList<>();
        AtomicInteger timesOfValid = new AtomicInteger(); //20级占用老板的的人数
        for (RankDTO self : rankDTOList) {
            if (!self.getUserLocation().equals("5109")) {
                timesOfValid.getAndIncrement();
            }
            resList.add(self);
            if (timesOfValid.get() >= 5 || resList.size() >= 10) break;
        }
        return resList;
    }

    public List<RankDTO> getTopFiveOfOldMan() {

        return rankMapper.getTopOldFive(timeHelper.getNowWeek(), systemInfo.getTerm(), systemInfo.getGrade());
    }

    public AttendanceRank selectByUserIdAndWeek(Long userId, int week) {
        return rankMapper.selectByUserIdAndWeek(userId, week, systemInfo.getTerm());
    }

    public void insert(AttendanceRank rank) {
        rankMapper.insert(rank);
    }

    public void updateById(AttendanceRank rank) {
        rankMapper.updateById(rank);
    }

    public List<RecordDTO> getOnlineUserList() {

        List<RecordDTO> list = recordMapper.selectOnlineRecord();
        Collections.shuffle(list);
        return list;
    }


    public ByteArrayOutputStream generateLastWeekRankExcelBytes(String trem, int week) {
        List<RankExport> newWeekRank = attendanceRankMapper.getNewWeekRank(trem, String.valueOf(week), systemInfo.getGrade());
        List<RankExport> oldWeekRank = attendanceRankMapper.getOldWeekRank(trem, String.valueOf(week), systemInfo.getGrade());

        log.info(newWeekRank.toString());
        log.info(oldWeekRank.toString());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ExcelWriter excelWriter = EasyExcel.write(out, RankExport.class).build();
            WriteSheet newRank = EasyExcel.writerSheet( "新人").build();
            WriteSheet oldRank = EasyExcel.writerSheet( "老人").build();
            excelWriter.write(newWeekRank, newRank);
            excelWriter.write(oldWeekRank, oldRank);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return out;
    }


    public void dowRank(String password, String trem, Integer week, HttpServletResponse resp) throws IOException {
        //默认值
        trem = trem == null ? systemInfo.getTerm() : trem;
        week = week == null ? -1 : week;
        if (!systemInfo.getPassword().equals(password)) {
            resp.setHeader("Content-Type", "application/json");
            Map<String, Object> map = new HashMap<>();
            map.put("code", CExceptionEnum.PASSWORD_INCORRECT.getCode());
            map.put("msg", CExceptionEnum.PASSWORD_INCORRECT.getMsg());
            objectMapper.writeValue(resp.getOutputStream(), map);
            return;
        }
        int nowWeek = timeHelper.getNowWeek();
//        System.out.println(nowWeek);
        boolean thisWeek = week == 0 || week == nowWeek;

        nowWeek = week <= 0 ? week + nowWeek : week;
        if (systemInfo.getTerm().equals(trem) && (nowWeek > timeHelper.getNowWeek() || nowWeek <= 0)) {
            //日期异常
            resp.setHeader("Content-Type", "application/json");
            Map<String, Object> map = new HashMap<>();
            map.put("code", CExceptionEnum.DATE_ERR.getCode());
            map.put("msg", CExceptionEnum.DATE_ERR.getMsg());
            objectMapper.writeValue(resp.getOutputStream(), map);
            return;
        }
        List<RankExport> newWeekRank = attendanceRankMapper.getNewWeekRank(trem, String.valueOf(nowWeek),
                systemInfo.getGrade());
        List<RankExport> oldWeekRank = attendanceRankMapper.getOldWeekRank(trem, String.valueOf(nowWeek),
                systemInfo.getGrade());
        String[] t = trem.split("_");
        String lable = t[0] + "-" + t[1] + ("1".equals(t[2]) ? "上学期" : "下学期");
        resp.setHeader("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(lable + "第" + nowWeek + "周" + ".xlsx", "UTF-8"));
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(resp.getOutputStream(), RankExport.class).build();
            WriteSheet newRank = EasyExcel.writerSheet(thisWeek ? "新人(本周未截止)" : "新人").build();
            WriteSheet oldRank = EasyExcel.writerSheet(thisWeek ? "老人(本周未截止)" : "老人").build();
            excelWriter.write(newWeekRank, newRank);
            excelWriter.write(oldWeekRank, oldRank);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 强制写入
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }

    public void getTermRankWithWeeklyStats(String password, String trem, Integer startWeek,
                                           Integer endWeek, HttpServletResponse resp) throws IOException {
        //默认值
        trem = trem == null ? systemInfo.getTerm() : trem;

        if (!systemInfo.getPassword().equals(password)) {
            resp.setHeader("Content-Type", "application/json");
            Map<String, Object> map = new HashMap<>();
            map.put("code", CExceptionEnum.PASSWORD_INCORRECT.getCode());
            map.put("msg", CExceptionEnum.PASSWORD_INCORRECT.getMsg());
            objectMapper.writeValue(resp.getOutputStream(), map);
            return;
        }
        int nowWeek = timeHelper.getNowWeek();
//        System.out.println(nowWeek);

        if (systemInfo.getTerm().equals(trem) && (nowWeek > timeHelper.getNowWeek() || nowWeek <= 0)) {
            //日期异常
            resp.setHeader("Content-Type", "application/json");
            Map<String, Object> map = new HashMap<>();
            map.put("code", CExceptionEnum.DATE_ERR.getCode());
            map.put("msg", CExceptionEnum.DATE_ERR.getMsg());
            objectMapper.writeValue(resp.getOutputStream(), map);
            return;
        }
        List<TermRankExport> newWeekRank = attendanceRankMapper.getTermRankWithWeeklyStats(trem, systemInfo.getGrade(), startWeek, endWeek);

        String[] t = trem.split("_");
        String lable = t[0] + "-" + t[1] + ("1".equals(t[2]) ? "上学期" : "下学期");
        resp.setHeader("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(lable + "第" + nowWeek + "周" + ".xlsx", "UTF-8"));
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(resp.getOutputStream(), RankExport.class).build();
            WriteSheet newRank = EasyExcel.writerSheet(String.format("新人，从%d周到%d周", startWeek, endWeek)).build();
            excelWriter.write(newWeekRank, newRank);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 强制写入
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }
}
