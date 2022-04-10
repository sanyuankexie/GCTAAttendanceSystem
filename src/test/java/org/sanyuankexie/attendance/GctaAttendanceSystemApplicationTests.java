package org.sanyuankexie.attendance;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.util.StringUtil;
import org.junit.jupiter.api.Test;
import org.sanyuankexie.attendance.common.exception.CExceptionEnum;
import org.sanyuankexie.attendance.common.helper.TimeHelper;
import org.sanyuankexie.attendance.common.job.AutoSignOutJob;
import org.sanyuankexie.attendance.mapper.AttendanceRankMapper;
import org.sanyuankexie.attendance.model.RankExport;
import org.sanyuankexie.attendance.model.SystemInfo;
import org.sanyuankexie.attendance.service.AttendanceRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class GctaAttendanceSystemApplicationTests {
    @Resource
    private AttendanceRankService rankService;

    @Test
    void contextLoads() throws NoSuchFieldException, IllegalAccessException {
//        User user = new User();
//        user.setEmail("sb");
//        ResultVO<Object> resultVO = new ResultVO<>(user, 1, "1");
//        System.out.println(resultVO.getData() instanceof User);
//        System.out.println(ClassHelper.getObjectFieldValue(resultVO.getData(), "email"));
//        Object res = rankService.getTopFive();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(new Date(1603620726992L)));

    }
    @Autowired
    AttendanceRankMapper attendanceRankMapper;

    @Autowired
    SystemInfo systemInfo;
    @Autowired
    TimeHelper timeHelper;
    @Autowired
    ObjectMapper objectMapper;

    public void dowRank( String trem, Integer week, HttpServletResponse resp) throws IOException {
        //默认值
        trem=trem==null?systemInfo.getTerm():trem;
//        week=week==null?-1:week;
//        if (!systemInfo.getPassword().equals(password)){
//            resp.setHeader("Content-Type","application/json");
//            Map<String,Object> map=new HashMap<>();
//            map.put("code", CExceptionEnum.PASSWORD_INCORRECT.getCode());
//            map.put("msg",CExceptionEnum.PASSWORD_INCORRECT.getMsg());
//            objectMapper.writeValue(resp.getOutputStream(),map);
//            return;
//        }
//        int nowWeek = timeHelper.getNowWeek();
//
//        if (week==0||week==nowWeek){
//            resp.setHeader("Content-Type","application/json");
//            Map<String,Object> map=new HashMap<>();
//            map.put("code",CExceptionEnum.WEEK_NO_END.getCode());
//            map.put("msg",CExceptionEnum. WEEK_NO_END.getMsg());
//            objectMapper.writeValue(resp.getOutputStream(),map);
//            return;
//        }
//
//        nowWeek=week<0?week+nowWeek:week;
//        if (systemInfo.getTerm().equals(trem)&&(nowWeek>= timeHelper.getNowWeek()||nowWeek<=0)){
//            //日期异常
//            resp.setHeader("Content-Type","application/json");
//            Map<String,Object> map=new HashMap<>();
//            map.put("code",CExceptionEnum.DATE_ERR.getCode());
//            map.put("msg",CExceptionEnum. DATE_ERR.getMsg());
//            objectMapper.writeValue(resp.getOutputStream(),map);
//            return;
//        }
        List<RankExport> newWeekRank = attendanceRankMapper.getAllNewWeekRanMine(systemInfo.getTerm(),
                systemInfo.getLeve() * 100000000L, StringUtil.join(systemInfo.getIsLeve(), ","));
//        List<RankExport> oldWeekRank = attendanceRankMapper.getOldWeekRank(systemInfo.getTerm(), String.valueOf(nowWeek),
//                systemInfo.getLeve() * 100000000L, StringUtil.join(systemInfo.getIsLeve(), ","));
        ExcelWriter excelWriter = null;
        String[] t = trem.split("_");
        String lable =t[0]+"-"+t[1]+("1".equals(t[2])?"上学期":"下学期");
//        resp.setHeader("Content-Disposition", "attachment;filename=" +  URLEncoder.encode(lable + ".xlsx","UTF-8"));


//        Map<String,RankExport> map=new HashMap<>();
//
//        for (RankExport r:newWeekRank){
//            RankExport rankExport = map.get(r.getName());
//            if (r!=null){
//                rankExport
//
//            }
//
//
//
//        }

        try {
            excelWriter = EasyExcel.write("E:\\work\\"+trem+"新生总时长.xlsx", RankExport.class).build();
            WriteSheet newRank = EasyExcel.writerSheet("新人").build();
//            WriteSheet oldRank=EasyExcel.writerSheet("老人").build();
            excelWriter.write(newWeekRank, newRank);
//            excelWriter.write(oldWeekRank,oldRank);
        } finally {
            // 强制写入
            if (excelWriter != null) {
                excelWriter.finish();
            }

        }
    }
    @Autowired
    AutoSignOutJob autoSignOutJob;
//    @Test
//    void sout(){
//        autoSignOutJob.executeInternal();
//    }
    @Test
    void getNewRank() throws FileNotFoundException {

        List<RankExport> newWeekRank = attendanceRankMapper.getNewWeekRank(systemInfo.getTerm(), String.valueOf(timeHelper.getNowWeek()),
                systemInfo.getLeve() * 100000000L, StringUtil.join(systemInfo.getIsLeve(), ","));
        List<RankExport> oldWeekRank = attendanceRankMapper.getOldWeekRank(systemInfo.getTerm(), String.valueOf(timeHelper.getNowWeek()),
                systemInfo.getLeve() * 100000000L, StringUtil.join(systemInfo.getIsLeve(), ","));
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write("E:\\work\\223.xlsx", RankExport.class).build();
            WriteSheet newRank = EasyExcel.writerSheet("新人").build();
            WriteSheet oldRank=EasyExcel.writerSheet("老人").build();
//        EasyExcel.write("E:\\work\\223.xlsx", RankExport.class).build().write(newWeekRank,newRank);
//            WriteSheet writeSheet = EasyExcel.writerSheet("模板").build();
            excelWriter.write(newWeekRank, newRank);
//            excelWriter.write(oldRank)
            excelWriter.write(oldWeekRank,oldRank);

        } finally {
            // 千万别忘记finish 会帮忙关闭流
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }


    }

    @Test
    void getMine() throws IOException {
        dowRank("2021_2022_1",null,null);
    }

}
