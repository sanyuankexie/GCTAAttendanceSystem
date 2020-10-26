package org.sanyuankexie.attendance.model;

public class AttendanceRank {
    private String id;
    private Long userId;
    private Integer week;
    private Long totalTime;

    public AttendanceRank(String id, Long userId, Integer week, Long totalTime) {
        this.id = id;
        this.userId = userId;
        this.week = week;
        this.totalTime = totalTime;
    }

    public AttendanceRank() {
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

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public Long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Long totalTime) {
        this.totalTime = totalTime;
    }
}
