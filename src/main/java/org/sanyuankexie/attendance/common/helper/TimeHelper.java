package org.sanyuankexie.attendance.common.helper;

public class TimeHelper {
    // This is 2020-10-26 00:00:00
    // It was be recorded as start week
    private static final Long DEFAULT_TIME = 1603641600000L;

    public static int getWeek(Long now) {
        long res = (now - DEFAULT_TIME) / 1000;
        return (int) res / (3600 * 24 * 7) + 1;
    }

    public static int getNowWeek() {
        return getWeek(System.currentTimeMillis());
    }
}
