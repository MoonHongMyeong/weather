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
}
