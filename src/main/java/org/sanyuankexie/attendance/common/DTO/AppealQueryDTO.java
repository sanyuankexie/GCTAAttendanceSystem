package org.sanyuankexie.attendance.common.DTO;

import lombok.Data;

@Data
public class AppealQueryDTO {
    private String appealId; // 对应申诉编号
    private String name; // 申诉人姓名
    private String department; // 申诉人部门
    private String term;
    private Long studentId; //申诉人学号
    private Integer status; // 申诉状态
    private Long operator; // 申诉处理者的学号
}
