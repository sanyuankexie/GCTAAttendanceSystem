package org.sanyuankexie.attendance.model;


import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class User {
    @ExcelProperty("学号")
    @NotNull(message = "userId 不能为空")
    private Long id;
    @NotNull(message = "userName 不能为空")
    @ExcelProperty("姓名")
    private String name;
    @NotNull(message = "部门不能为空")
    @ExcelProperty("部门")
    private String dept;
    @NotNull(message = "位置不能为空")
    @ExcelProperty("位置")
    private String location;
    @NotNull(message = "邮箱地址不能为空")
    @ExcelProperty("邮箱地址")
    private String email;
    @ExcelProperty("github")
    private String githubId;
    @ExcelProperty("年级(例如：22级填22)")
    private String grade;
    @NotNull(message = "学习方向不能为空")
    @ExcelProperty("方向")
    private String learn;
    @ExcelProperty("权限")
    private Integer role;
}
