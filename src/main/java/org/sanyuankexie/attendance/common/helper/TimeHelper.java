package org.sanyuankexie.attendance.common.helper;

public class TimeHelper {
    // This is 2021-03-01 00:00:00
    // It was be recorded as start week
    private static final Long DEFAULT_TIME = 1614556800000L;

    public static int getWeek(Long now) {
        long res = (now - DEFAULT_TIME) / 1000;
        return (int) res / (3600 * 24 * 7) + 1;
    }

    public static int getNowWeek() {
        return getWeek(System.currentTimeMillis());
    }
}
