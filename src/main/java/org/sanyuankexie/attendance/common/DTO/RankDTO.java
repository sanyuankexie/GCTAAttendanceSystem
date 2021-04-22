package org.sanyuankexie.attendance.common.DTO;

public class RankDTO {
    private Long userId;
    private String userName;
    private String userDept;
    private String userLocation;
    private Object totalTime;
    private Object accumulatedTime;
    private int week;

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

    public Object getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Object totalTime) {
        this.totalTime = totalTime;
    }

    public Object getAccumulatedTime() {
        return accumulatedTime;
    }

    public void setAccumulatedTime(Object accumulatedTime) {
        this.accumulatedTime = accumulatedTime;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }
}
