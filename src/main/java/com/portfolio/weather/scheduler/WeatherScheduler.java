package com.portfolio.weather.scheduler;

import com.portfolio.weather.scheduler.data.type.FileType;
import com.portfolio.weather.scheduler.service.VillageFcstInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherScheduler {
    private final VillageFcstInfoService villageFcstInfoService;

    /** 
     * 단기예보
     * - 발표시각 02:00, 05:00, 08:00, 11:00, 14:00, 17:00, 20:00, 23:00 (1일 8회)
     * - API 제공 시간(~이후) : 02:10, 05:10, 08:10, 11:10, 14:10, 17:10, 20:10, 23:10
     * */ 
    @Scheduled(cron = "0 13 */3 * * *")
    public void executeVillageForecast(){
        if(!villageFcstInfoService.isLatestVersion(FileType.SHRT)){

        }
    }

    /** 초단기실황 
     * 매시간 정시에 생성되고 10분마다 최신 정보로 업데이트
    */
    @Scheduled(cron = "0 15 */1 * * *")
    public void executeUltraShortObservation(){
        if(!villageFcstInfoService.isLatestVersion(FileType.ODAM)){

        }
    }
    /** 초단기예보 */
    @Scheduled(cron = "0 33 */1 * * *")
    public void executeUltraShortForecast(){
        if(!villageFcstInfoService.isLatestVersion(FileType.VSRT)){

        }
    }
}
