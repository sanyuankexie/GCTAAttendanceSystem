package org.sanyuankexie.attendance.common.DTO;

public class RecordDTO{
    private Long userId;
    private String userName;
    private String userDept;
    private String userLocation;
    private Object start;
    private Object end;
    private Object status; // 1 -> Online, 0 -> Offline, -1 -> Be reported
    private Object accumulatedTime;

    public Object getAccumulatedTime() {
        return accumulatedTime;
    }

    public void setAccumulatedTime(Object accumulatedTime) {
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

    public Object getStart() {
        return start;
    }

    public void setStart(Object start) {
        this.start = start;
    }

    public Object getEnd() {
        return end;
    }

    public void setEnd(Object end) {
        this.end = end;
    }

    public Object getStatus() {
        return status;
    }

    public void setStatus(Object status) {
        this.status = status;
    }
}
