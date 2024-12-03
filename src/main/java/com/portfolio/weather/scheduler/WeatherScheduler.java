package com.portfolio.weather.scheduler;

import com.portfolio.weather.scheduler.data.DaejeonCoordinate;
import com.portfolio.weather.scheduler.data.Location;
import com.portfolio.weather.scheduler.service.MidFcstInfoService;
import com.portfolio.weather.scheduler.service.VillageFcstInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherScheduler {
    private final VillageFcstInfoService villageFcstInfoService;
    private final MidFcstInfoService midFcstInfoService;

    /** 
     * 단기예보
     * - 발표시각 02:00, 05:00, 08:00, 11:00, 14:00, 17:00, 20:00, 23:00 (1일 8회)
     * - API 제공 시간(~이후) : 02:10, 05:10, 08:10, 11:10, 14:10, 17:10, 20:10, 23:10
     * */ 
    @Scheduled(cron = "0 13 */3 * * *")
    public void executeVillageForecast(){
        // 대전시 모든 좌표에 대해 예보 조회
        for (DaejeonCoordinate coord : DaejeonCoordinate.values()) {
            try {
                villageFcstInfoService.fetchAndSaveShrt(coord.getNx(), coord.getNy());
                log.info("단기예보 조회 완료 - 좌표: ({}, {})", coord.getNx(), coord.getNy());
            } catch (Exception e) {
                // 한 좌표에서 잘못되더라도 나머지는 실행하도록
                log.error("단기예보 조회 실패 - 좌표: ({}, {}), 에러: {}", coord.getNx(), coord.getNy(), e.getMessage());
            }
        }
    }

    /**
     * 중기예보(중기육상예보조회)
     * */
    @Scheduled(cron = "0 30 6/12 * * *")
    public void executeMidLandForecast(){
        for (Location location : Location.values()){
            midFcstInfoService.fetchAndSaveMidLandForecast(location.getRegionId());
        }
    }

    /**
     * 중기예보(중기기온조회)
     * */
    @Scheduled(cron = "0 15 6/12 * * *")
    public void executeMidTemperature(){
        for (Location location : Location.values()){
            midFcstInfoService.fetchAndSaveMidTempForecast(location.getRegionId());
        }
    }
}
