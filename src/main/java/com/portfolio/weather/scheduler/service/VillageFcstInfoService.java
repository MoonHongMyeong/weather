package com.portfolio.weather.scheduler.service;

import com.portfolio.weather.scheduler.data.type.SHRT;
import com.portfolio.weather.scheduler.mapper.VillageForecastMapper;
import com.portfolio.weather.scheduler.utils.ApiResponseParser;
import com.portfolio.weather.scheduler.utils.BaseDateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VillageFcstInfoService {
    private final VillageForecastMapper vfMapper;
    private final RestTemplate restTemplate;
    private final ApiResponseParser apiResponseParser;

    @Value("${kweather.api-hub.auth-key}")
    private String AUTH_KEY;
    @Value("${kweather.service.villageForecastService.villageForecast}")
    private String VILLAGE_FORECAST_URL;

    public void fetchAndSaveShrt(String nx, String ny) {
        int SHRT_ROWS = SHRT.values().length;
        // 1. API 호출
        LocalDateTime now = LocalDateTime.now();
        int numOfRows = SHRT_ROWS * 24; // 24시간 후까지 조회
        String apiUrl = VILLAGE_FORECAST_URL +
                "?authKey=" + AUTH_KEY +
                "&numOfRows=" + numOfRows +
                "&pageNo=1" +
                "&dataType=JSON" +
                "&base_date=" + BaseDateTimeUtil.getBaseDate(now) +
                "&base_time=" + BaseDateTimeUtil.getBaseTime(now) +
                "&nx=" + nx +
                "&ny=" + ny;
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        // 2. 응답 파싱
        List<Map<String, Object>> itemList = apiResponseParser.parseResponseJson(response);

        // 3. 데이터 저장
        itemList.forEach(item -> {
            String category = (String) item.get("category");
            
            try {
                SHRT shrtCategory = SHRT.valueOf(category);
                Map<String, Object> forecast = new HashMap<>();
                forecast.put("nx", nx);
                forecast.put("ny", ny);
                forecast.put("baseDate", item.get("baseDate"));
                forecast.put("baseTime", item.get("baseTime"));
                forecast.put("fcstDate", item.get("fcstDate"));
                forecast.put("fcstTime", item.get("fcstTime"));
                forecast.put("category", category);
                forecast.put("fcstValue", item.get("fcstValue"));
                
                log.info("예보 데이터 - {}: {} ({})", 
                    shrtCategory.getDescription(), 
                    item.get("fcstValue"),
                    item.get("fcstTime")
                );
                
                vfMapper.mergeShrt(forecast);
            } catch (IllegalArgumentException e) {
                log.warn("알 수 없는 category: {}", category);
            } catch (PersistenceException e) {
                log.warn("DB 저장 중 에러 발생 - category: {}, error: {}", 
                    category, e.getMessage());
            }
        });
    }
}
