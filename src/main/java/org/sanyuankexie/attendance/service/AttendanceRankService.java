package org.sanyuankexie.attendance.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.util.StringUtil;
import org.sanyuankexie.attendance.common.DTO.RankDTO;
import org.sanyuankexie.attendance.common.DTO.RecordDTO;
import org.sanyuankexie.attendance.common.exception.CExceptionEnum;
import org.sanyuankexie.attendance.common.helper.TimeHelper;
import org.sanyuankexie.attendance.mapper.AttendanceRankMapper;
import org.sanyuankexie.attendance.mapper.AttendanceRecordMapper;
import org.sanyuankexie.attendance.model.AttendanceRank;
import org.sanyuankexie.attendance.model.RankExport;
import org.sanyuankexie.attendance.model.SystemInfo;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
        List<RankDTO> rankDTOList = rankMapper.getTopFive(timeHelper.getNowWeek(), systemInfo.getLeve()*100000000L, (systemInfo.getLeve()+1)*100000000L, systemInfo.getTerm(),
                StringUtil.join(systemInfo.getIsLeve(), ","));
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

        return rankMapper.getTopOldFive(timeHelper.getNowWeek(), 0L, systemInfo.getLeve()*100000000L,systemInfo.getTerm(),StringUtil.join( systemInfo.getIsLeve(),","));
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
        return recordMapper.selectOnlineRecord();
    }


    public void dowRank(String password, String trem, Integer week, HttpServletResponse resp) throws IOException {
        //默认值
       trem=trem==null?systemInfo.getTerm():trem;
       week=week==null?-1:week;
        if (!systemInfo.getPassword().equals(password)){
            resp.setHeader("Content-Type","application/json");
            Map<String,Object> map=new HashMap<>();
            map.put("code",CExceptionEnum.PASSWORD_INCORRECT.getCode());
            map.put("msg",CExceptionEnum.PASSWORD_INCORRECT.getMsg());
            objectMapper.writeValue(resp.getOutputStream(),map);
            return;
        }
        int nowWeek = timeHelper.getNowWeek();

        if (week==0||week==nowWeek){
            resp.setHeader("Content-Type","application/json");
            Map<String,Object> map=new HashMap<>();
            map.put("code",CExceptionEnum.WEEK_NO_END.getCode());
            map.put("msg",CExceptionEnum. WEEK_NO_END.getMsg());
            objectMapper.writeValue(resp.getOutputStream(),map);
            return;
        }

        nowWeek=week<0?week+nowWeek:week;
        if (systemInfo.getTerm().equals(trem)&&(nowWeek>= timeHelper.getNowWeek()||nowWeek<=0)){
            //日期异常
            resp.setHeader("Content-Type","application/json");
            Map<String,Object> map=new HashMap<>();
            map.put("code",CExceptionEnum.DATE_ERR.getCode());
            map.put("msg",CExceptionEnum. DATE_ERR.getMsg());
            objectMapper.writeValue(resp.getOutputStream(),map);
            return;
        }
        List<RankExport> newWeekRank = attendanceRankMapper.getNewWeekRank(systemInfo.getTerm(), String.valueOf(nowWeek),
                systemInfo.getLeve() * 100000000L, StringUtil.join(systemInfo.getIsLeve(), ","));
        List<RankExport> oldWeekRank = attendanceRankMapper.getOldWeekRank(systemInfo.getTerm(), String.valueOf(nowWeek),
                systemInfo.getLeve() * 100000000L, StringUtil.join(systemInfo.getIsLeve(), ","));
        ExcelWriter excelWriter = null;
        String[] t = trem.split("_");
        String lable =t[0]+"-"+t[1]+("1".equals(t[2])?"上学期":"下学期");
        resp.setHeader("Content-Disposition", "attachment;filename=" +  URLEncoder.encode(lable+"第"+nowWeek+"周" + ".xlsx","UTF-8"));
        try {
            excelWriter = EasyExcel.write(resp.getOutputStream(), RankExport.class).build();
            WriteSheet newRank = EasyExcel.writerSheet("新人").build();
            WriteSheet oldRank=EasyExcel.writerSheet("老人").build();
            excelWriter.write(newWeekRank, newRank);
            excelWriter.write(oldWeekRank,oldRank);
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
