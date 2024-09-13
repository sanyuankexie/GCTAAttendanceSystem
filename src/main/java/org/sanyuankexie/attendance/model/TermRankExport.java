package org.sanyuankexie.attendance.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentFontStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;
import lombok.Data;

@Data
@HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 9)
@HeadFontStyle(fontHeightInPoints = 11, bold = BooleanEnum.FALSE, fontName = "微软雅黑")
@ContentFontStyle(fontName = "微软雅黑")
public class TermRankExport {

    @ExcelProperty("学号")
    private Long id;

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("部门")
    private String dept;

    @ExcelProperty("总时长")
    private String totalTime; // 以小时为单位

    @ExcelProperty("周数")
    private Integer totalWeeks;

    @ExcelProperty("每周平均时长")
    private String avgTimePerWeek; // 以小时为单位

    @ExcelProperty("每周最大时长")
    private String maxTimePerWeek; // 以小时为单位

    @ExcelProperty("每周最小时长")
    private String minTimePerWeek; // 以小时为单位

}
