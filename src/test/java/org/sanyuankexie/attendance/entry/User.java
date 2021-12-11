package org.sanyuankexie.attendance.entry;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
public class User {
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
