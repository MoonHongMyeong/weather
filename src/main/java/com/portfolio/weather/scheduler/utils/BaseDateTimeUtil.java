package com.portfolio.weather.scheduler.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaseDateTimeUtil {
    public static String getBaseTime(LocalDateTime now) {
        int minute = now.getMinute();

        // 현재 분을 10분 단위로 내림
        int adjustedMinute = (minute / 10) * 10;

        // 시간과 분을 HHmm 형식으로 포맷팅
        return String.format("%02d%02d", now.getHour(), adjustedMinute);
    }

    public static String getBaseDate(LocalDateTime now) {
        return now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public static String getBaseDateTime(LocalDateTime now) {
        return now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
    }

    public static String getBaseTimeSHRT(LocalDateTime now) {
        int currentHour = now.getHour();
        switch (currentHour){
            case 1, 2, 3:
                return "0200";
            case 4, 5, 6:
                return "0500";
            case 7, 8, 9:
                return "0800";
            case 10, 11, 12:
                return "1100";
            case 13, 14, 15:
                return "1400";
            case 16, 17, 18:
                return "1700";
            case 19, 20, 21:
                return "2000";
            case 22, 23, 0:
                return "2300";
            default:
                throw new RuntimeException("시간 치환에 문제가 생겼습니다.");
        }
    }
}
