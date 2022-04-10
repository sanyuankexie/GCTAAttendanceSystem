package org.sanyuankexie.attendance.common.helper;

import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;


public class TimeHelper {
    // This is 2021-03-01 00:00:00
    // It was be recorded as start week

    private final Long DEFAULT_TIME ;

    private   int getWeek(Long now) {
        long res = (now - DEFAULT_TIME) / 1000;
        return (int) res / (3600 * 24 * 7) + 1;
    }

    public  int getNowWeek() {
        return getWeek(System.currentTimeMillis());
    }
    public TimeHelper(Long DEFAULT_TIME){
        this.DEFAULT_TIME=DEFAULT_TIME;
    }


    Integer[] midnight={6,7};

    public boolean noAllSign(Long time){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        int week=calendar.get(Calendar.DAY_OF_WEEK);
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        long min = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 30);
        long max = calendar.getTimeInMillis();
        if (Arrays.asList(midnight).contains(week)){
            max+=1000*60*30;
        }
        return  time<min||time>max;
    }

    public boolean noStart(Long time){

        return DEFAULT_TIME>time;
    }
}
