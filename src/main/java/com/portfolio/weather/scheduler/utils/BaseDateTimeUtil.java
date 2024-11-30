package com.portfolio.weather.scheduler.utils;

import com.portfolio.weather.scheduler.data.type.AnnounceTime;

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
        if ( now.getHour() < 2 ){
            now = now.minusDays(1);
        }
        return now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public static String getBaseDateTime(LocalDateTime now) {
        return now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
    }

    public static String getBaseTimeSHRT(LocalDateTime now) {
        int currentHour = now.getHour();
        return switch (currentHour) {
            case 2, 3, 4 -> AnnounceTime.H02.getStrTime();
            case 5, 6, 7 -> AnnounceTime.H05.getStrTime();
            case 8, 9, 10 -> AnnounceTime.H08.getStrTime();
            case 11, 12, 13 -> AnnounceTime.H11.getStrTime();
            case 14, 15, 16 -> AnnounceTime.H14.getStrTime();
            case 17, 18, 19 -> AnnounceTime.H17.getStrTime();
            case 20, 21, 22 -> AnnounceTime.H20.getStrTime();
            case 23, 0, 1 -> AnnounceTime.H23.getStrTime();
            default -> throw new RuntimeException("시간 치환에 문제가 생겼습니다.");
        };
    }
}
