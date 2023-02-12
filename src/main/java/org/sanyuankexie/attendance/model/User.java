package org.sanyuankexie.attendance.model;


import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class User {
    @ExcelProperty("学号")
    private   Long id;
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
    @ExcelProperty("年级(例如：22级填22)")
    private String grade;
}
