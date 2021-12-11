package org.sanyuankexie.attendance;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import org.apache.poi.util.StringUtil;
import org.junit.jupiter.api.Test;
import org.sanyuankexie.attendance.common.helper.TimeHelper;
import org.sanyuankexie.attendance.mapper.AttendanceRankMapper;
import org.sanyuankexie.attendance.model.RankExport;
import org.sanyuankexie.attendance.model.SystemInfo;
import org.sanyuankexie.attendance.service.AttendanceRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

}
