package com.portfolio.weather.scheduler.utils;

import com.portfolio.weather.scheduler.data.type.AnnounceTime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaseDateTimeUtil {
    public static String getBaseTime(LocalDateTime now) {
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
            default -> throw new RuntimeException("시간 치환에 문제가 생겼습니다 : getBaseTimeSHRT : " + currentHour);
        };
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

    public static String getTimeForEnvironmentalIndex(LocalDateTime now) {
        int currentHour = now.getHour();
        return switch (currentHour){
            case 0, 1, 2 -> now.withHour(0).format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
            case 3, 4, 5 -> now.withHour(3).format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
            case 6, 7, 8 -> now.withHour(6).format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
            case 9, 10, 11 -> now.withHour(9).format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
            case 12, 13, 14 -> now.withHour(12).format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
            case 15, 16, 17 -> now.withHour(15).format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
            case 18, 19, 20 -> now.withHour(18).format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
            case 21, 22, 23 -> now.withHour(21).format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
            default -> throw new IllegalStateException("시간 치환에 문제가 있습니다 : getTimeEnvironmentalIndex : " + currentHour);
        };
    }
}
