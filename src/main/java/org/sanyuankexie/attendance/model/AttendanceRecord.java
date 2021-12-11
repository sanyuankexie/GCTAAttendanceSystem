package org.sanyuankexie.attendance.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRecord {
    private String id;
    private Long userId;
    private Long start;
    private Long end;
    private int status; // 1 -> Online, 0 -> Offline, -1 -> Be reported
    private Long operatorId;
    private String term;


}
