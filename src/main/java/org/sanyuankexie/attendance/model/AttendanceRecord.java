package org.sanyuankexie.attendance.model;

public class AttendanceRecord {
    private String id;
    private Long userId;
    private Long start;
    private Long end;
    private int status; // 1 -> Online, 0 -> Offline, -1 -> Be reported
    private Long operatorId;

    public AttendanceRecord() {
    }

    public AttendanceRecord(String id, Long userId, Long start, Long end, int status, Long operatorId) {
        this.id = id;
        this.userId = userId;
        this.start = start;
        this.end = end;
        this.status = status;
        this.operatorId = operatorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }
}
