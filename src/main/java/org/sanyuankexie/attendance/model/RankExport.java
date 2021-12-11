package org.sanyuankexie.attendance.model;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentFontStyle;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;
import lombok.Data;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

@Data
@HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 9)
@HeadFontStyle(fontHeightInPoints = 11,bold = BooleanEnum.FALSE,fontName = "微软雅黑")
@ContentFontStyle(fontName = "微软雅黑")
public class RankExport {

    @ExcelProperty("学号")
    private Long id;
    @ExcelProperty("姓名")
    private String name;
    @ExcelProperty("部门")
    private String dept;
    @ExcelProperty("总时长")
    private String totalTime;
    @ExcelProperty("周序")
    private Integer week;


}
