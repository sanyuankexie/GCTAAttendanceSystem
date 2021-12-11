package org.sanyuankexie.attendance.entry;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class DemoData {
    /**
     * 强制读取第三个 这里不建议 index 和 name 同时用，要么一个对象只用index，要么一个对象只用name去匹配
     */
//    @ExcelProperty("次数")
//    private Double doubleData;
//    /**
//     * 用名字去匹配，这里需要注意，如果名字重复，会导致只有一个字段读取到数据
//     */
//    @ExcelProperty("字符串标题")
//    private String string;
//    @ExcelProperty("日期标题")
//    private Date date;
        @ExcelProperty("学号")
  private   String id;
    @ExcelProperty("姓名")
   private String name;
    @ExcelProperty("部门")
   private String dept;
    @ExcelProperty("位置")
   private String location;
    @ExcelProperty("邮箱地址")
   private String email;
    @ExcelProperty("github")
  private   String githubId;
}