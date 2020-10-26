package org.sanyuankexie.attendance.common.DTO;

public class RankDTO {
    private Long userId;
    private String userName;
    private String userDept;
    private String userLocation;
    private Long totalTime;
    private Long accumulatedTime;
    private int week;

    public Long getAccumulatedTime() {
        return accumulatedTime;
    }

    public void setAccumulatedTime(Long accumulatedTime) {
        this.accumulatedTime = accumulatedTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDept() {
        return userDept;
    }

    public void setUserDept(String userDept) {
        this.userDept = userDept;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public Long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Long totalTime) {
        this.totalTime = totalTime;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }
}
