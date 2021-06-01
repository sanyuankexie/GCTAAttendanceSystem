package org.sanyuankexie.attendance.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRank {
    private String id;
    private Long userId;
    private Integer week;
    private Long totalTime;

}
