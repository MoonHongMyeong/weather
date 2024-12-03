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
                "&reg=" + locationCode +
                "&tmfc=0&disp=1&help=0";
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        // 2. 응답 파싱
        List<Map<String, Object>> itemList = apiResponseParser.parseResponseText(response);

        // 3. 데이터 저장
        itemList.forEach(item -> {
                Map<String, Object> forecast = new HashMap<>();
                forecast.put("reg_id", item.get("REG_ID"));
                forecast.put("tm_fc", item.get("TM_FC"));
                forecast.put("tm_ef", item.get("TM_EF"));
                forecast.put("mod", item.get("MOD"));
                forecast.put("stn", item.get("STN"));
                forecast.put("sky", item.get("SKY"));
                forecast.put("pre", item.get("PRE"));
                forecast.put("conf", item.get("CONF"));
                forecast.put("wf", item.get("WF"));
                forecast.put("rn_st", (int)item.get("RN_ST") == -999 ? 0 : item.get("RN_ST"));
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
                "&reg=" + locationCode +
                "&tmfc=0&disp=1&help=0";
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        // 2. 응답 파싱
        List<Map<String, Object>> itemList = apiResponseParser.parseResponseText(response);

        // 3. 데이터 저장
        itemList.forEach(item -> {
            Map<String, Object> forecast = new HashMap<>();
            forecast.put("reg_id", item.get("REG_ID"));
            forecast.put("tm_fc", item.get("TM_FC"));
            forecast.put("tm_ef", item.get("TM_EF"));
            forecast.put("mod", item.get("MOD"));
            forecast.put("stn", item.get("STN"));
            forecast.put("min", item.get("MIN"));
            forecast.put("max", item.get("MAX"));
            forecast.put("min_l", (int)item.get("MIN_L") == -999 ? 0 : item.get("MIN_L"));
            forecast.put("min_h", (int)item.get("MIN_H") == -999 ? 0 : item.get("MIN_H"));
            forecast.put("max_l", (int)item.get("MAX_L") == -999 ? 0 : item.get("MAX_L"));
            forecast.put("max_h", (int)item.get("MAX_H") == -999 ? 0 : item.get("MAX_H"));   
            try {
                mfMapper.mergeMidTempFcst(forecast);
            } catch (PersistenceException e) {
                log.warn("DB 저장 중 에러 발생 - regId: {}, error: {}", 
                    item.get("regId"), e.getMessage());
            }
        });
    }
}
