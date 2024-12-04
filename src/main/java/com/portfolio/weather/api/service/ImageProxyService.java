package com.portfolio.weather.api.service;

import com.portfolio.weather.api.data.type.WeatherImageType;
import com.portfolio.weather.scheduler.utils.BaseDateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageProxyService {
    private final RestTemplate restTemplate;

    @Value("${kweather.api-hub.auth-key}")
    private String AUTH_KEY;

    @Value("${kweather.service.satelliteImgInfoService}")
    private String SATLLEITE_IMAGE_URL;

    @Value("${kweather.service.radarImgInfoService}")
    private String RADAR_IMAGE_URL;

    @Value("${kweather.service.lightningDistributionInfoService}")
    private String LIGHTNING_DISTRIBUTION_IMAGE_URL;


    public byte[] getWeatherImage(String type) {
        WeatherImageType imageType = WeatherImageType.valueOf(type.toUpperCase());
        LocalDateTime now = LocalDateTime.now();
        String baseTime = BaseDateTimeUtil.getTimeForImage(now);

        String apiUrl = switch (imageType) {
            case SATELLITE -> SATLLEITE_IMAGE_URL +
                    "?authKey=" + AUTH_KEY +
                    "&tm=" + baseTime +
                    "&obs=ir105&legend=0&size=300";

            case RADAR -> RADAR_IMAGE_URL +
                    "?authKey=" + AUTH_KEY +
                    "&tm=" + baseTime +
                    "&cmp=HSR&qcd=HSLP&obs=ECHD&color=C4&map=HR&legend=0&size=300&zoom_level=2&zoom_x=35&zoom_y=30";

            case LIGHTNING -> LIGHTNING_DISTRIBUTION_IMAGE_URL +
                    "?authKey=" + AUTH_KEY +
                    "&tm=" + baseTime +
                    "&map=HR&legend=0&size=300&size=300&zoom_level=11&zoom_x=35&zoom_y=30";
        };

        log.info("이미지 요청 URL: {}", apiUrl);
        return restTemplate.getForObject(apiUrl, byte[].class);
    }
}
