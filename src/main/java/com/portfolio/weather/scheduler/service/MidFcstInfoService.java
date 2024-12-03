package com.portfolio.weather.scheduler.service;

import com.portfolio.weather.scheduler.mapper.MidForecastMapper;
import com.portfolio.weather.scheduler.utils.ApiResponseParser;
import com.portfolio.weather.scheduler.utils.BaseDateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MidFcstInfoService {
    private final MidForecastMapper mfMapper;
    private final RestTemplate restTemplate;
    private final ApiResponseParser apiResponseParser;

    @Value("${kweather.api-hub.auth-key}")
    private String AUTH_KEY;
    @Value("${kweather.service.MidForecastService.landForecast}")
    private String LAND_FORECAST_URL;
    @Value("${kweather.service.MidForecastService.tempForecast}")
    private String TEMP_FORECAST_URL;

    public void fetchAndSaveMidLandForecast(String locationCode) {
        // 1. API 호출
        String apiUrl = LAND_FORECAST_URL +
                "?authKey=" + AUTH_KEY +
                "&pageNo=1" +
                "&numOfRows=10" +
                "&dataType=JSON" +
                "&regId=" + locationCode +
                "&tmFc=" + BaseDateTimeUtil.getBaseTimeMidFcst(LocalDateTime.now());
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        // 2. 응답 파싱
        List<Map<String, Object>> itemList = apiResponseParser.parseResponse(response);

        // 3. 데이터 저장
        itemList.forEach(item -> {
            Map<String, Object> forecast = new HashMap<>();
            forecast.put("regId", item.get("regId"));
            forecast.put("rnSt4Am", item.get("rnSt4Am"));
            forecast.put("rnSt4Pm", item.get("rnSt4Pm"));
            forecast.put("rnSt5Am", item.get("rnSt5Am"));
            forecast.put("rnSt5Pm", item.get("rnSt5Pm"));
            forecast.put("rnSt6Am", item.get("rnSt6Am"));
            forecast.put("rnSt6Pm", item.get("rnSt6Pm"));
            forecast.put("rnSt7Am", item.get("rnSt7Am"));
            forecast.put("rnSt7Pm", item.get("rnSt7Pm"));
            forecast.put("wf4Am", item.get("wf4Am"));
            forecast.put("wf4Pm", item.get("wf4Pm"));
            forecast.put("wf5Am", item.get("wf5Am"));
            forecast.put("wf5Pm", item.get("wf5Pm"));
            forecast.put("wf6Am", item.get("wf6Am"));
            forecast.put("wf6Pm", item.get("wf6Pm"));
            forecast.put("wf7Am", item.get("wf7Am"));
            forecast.put("wf7Pm", item.get("wf7Pm"));

            try {
                mfMapper.mergeMidLandFcst(forecast);
            } catch (PersistenceException e) {
                log.warn("DB 저장 중 에러 발생 - regId: {}, error: {}", 
                    item.get("regId"), e.getMessage());
            }
        });
    }

    public void fetchAndSaveMidTempForecast(String locationCode) {
        // 1. API 호출
        String apiUrl = TEMP_FORECAST_URL +
                "?authKey=" + AUTH_KEY +
                "&pageNo=1" +
                "&numOfRows=10" +
                "&dataType=JSON" +
                "&regId=" + locationCode +
                "&tmFc=" + BaseDateTimeUtil.getBaseTimeMidFcst(LocalDateTime.now());
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        // 2. 응답 파싱
        List<Map<String, Object>> itemList = apiResponseParser.parseResponse(response);

        // 3. 데이터 저장
        itemList.forEach(item -> {
            Map<String, Object> forecast = new HashMap<>();
            forecast.put("regId", item.get("regId"));
            forecast.put("taMin4", item.get("taMin4"));
            forecast.put("taMax4", item.get("taMax4"));
            forecast.put("taMin5", item.get("taMin5"));
            forecast.put("taMax5", item.get("taMax5"));
            forecast.put("taMin6", item.get("taMin6"));
            forecast.put("taMax6", item.get("taMax6"));
            forecast.put("taMin7", item.get("taMin7"));
            forecast.put("taMax7", item.get("taMax7"));

            try {
                mfMapper.mergeMidTempFcst(forecast);
            } catch (PersistenceException e) {
                log.warn("DB 저장 중 에러 발생 - regId: {}, error: {}", 
                    item.get("regId"), e.getMessage());
            }
        });
    }
}
