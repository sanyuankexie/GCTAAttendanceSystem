package org.sanyuankexie.attendance.common.DTO;


import lombok.Data;

@Data
public class RecordDTO{
    private Long userId;
    private String userName;
    private String userDept;
    private String userLocation;
    private Object start;
    private Object end;
    private Object status; // 1 -> Online, 0 -> Offline, -1 -> Be reported
    private Object accumulatedTime;


}
