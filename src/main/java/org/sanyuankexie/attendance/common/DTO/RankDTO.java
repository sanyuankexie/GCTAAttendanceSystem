package org.sanyuankexie.attendance.common.DTO;


import lombok.Data;

@Data
public class RankDTO {
    private Long userId;
    private String userName;
    private String userDept;
    private String userLocation;
    private Object totalTime;
    private Object accumulatedTime;
    private int week;


}
