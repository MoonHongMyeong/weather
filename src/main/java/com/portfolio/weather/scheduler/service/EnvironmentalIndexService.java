package com.portfolio.weather.scheduler.service;

import com.portfolio.weather.scheduler.data.type.EnvironmentalIndexType;
import com.portfolio.weather.scheduler.mapper.EnvironmentalIndexMapper;
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
public class EnvironmentalIndexService {
    private final EnvironmentalIndexMapper indexMapper;
    private final RestTemplate restTemplate;
    private final ApiResponseParser apiResponseParser;
    @Value("${kweather.api-hub.auth-key}")
    private String AUTH_KEY;
    @Value("${kweather.service.livingWeatherIndexService.UVIndex}")
    private String UV_INDEX_URL;
    @Value("${kweather.service.livingWeatherIndexService.AirDiffusionIndex}")
    private String AIR_DIFFUSION_URL;
    public void fetchAndSaveUvIndex(String areaNo) {
        String apiUrl = UV_INDEX_URL +
                "?authKey=" + AUTH_KEY +
                "&areaNo=" + areaNo +
                "&time=" + BaseDateTimeUtil.getTimeForEnvironmentalIndex(LocalDateTime.now()) +
                "pageNo=1&numOfRows=10&dataType=JSON";

        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
        List<Map<String, Object>> itemList = apiResponseParser.parseResponseJson(response);

        if (itemList.isEmpty()) {
            log.warn("자외선지수 데이터가 없습니다. areaNo : " + areaNo);
            return;
        }

        itemList.forEach(item -> {
            Map<String, Object> index = new HashMap<>();
            index.put("code", EnvironmentalIndexType.valueOf((String) item.get("code")).getName());
            index.put("areaNo", item.get("areaNo"));
            index.put("date", item.get("date"));
            index.put("h3", item.get("h3"));
            index.put("h6", item.get("h6"));
            index.put("h9", item.get("h9"));
            index.put("h12", item.get("h12"));
            index.put("h15", item.get("h15"));
            index.put("h18", item.get("h18"));
            index.put("h21", item.get("h21"));
            index.put("h24", item.get("h24"));
            index.put("h27", item.get("h27"));
            index.put("h30", item.get("h30"));
            index.put("h33", item.get("h33"));
            index.put("h36", item.get("h36"));
            index.put("h39", item.get("h39"));
            index.put("h42", item.get("h42"));
            index.put("h45", item.get("h45"));
            index.put("h48", item.get("h48"));
            index.put("h51", item.get("h51"));
            index.put("h54", item.get("h54"));
            index.put("h57", item.get("h57"));
            index.put("h60", item.get("h60"));
            index.put("h63", item.get("h63"));
            try{
                indexMapper.mergeIndex(index);
            } catch (PersistenceException e){
                log.warn("DB 저장 중 에러 발생 - areaNo: {}, error: {}",
                        item.get("areaNo"), e.getMessage());
            }
        });
    }

    public void fetchAndSaveAirDiffusionIndex(String areaNo) {
        String apiUrl = AIR_DIFFUSION_URL +
                "?authKey=" + AUTH_KEY +
                "&areaNo=" + areaNo +
                "&time=" + BaseDateTimeUtil.getTimeForEnvironmentalIndex(LocalDateTime.now()) +
                "pageNo=1&numOfRows=10&dataType=JSON";
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
        List<Map<String, Object>> itemList = apiResponseParser.parseResponseJson(response);

        if (itemList.isEmpty()) {
            log.warn("대기정체지수 데이터가 없습니다. areaNo : " + areaNo);
            return;
        }

        itemList.forEach(item -> {
            Map<String, Object> index = new HashMap<>();
            index.put("code", EnvironmentalIndexType.valueOf((String) item.get("code")).getName());
            index.put("areaNo", item.get("areaNo"));
            index.put("date", item.get("date"));
            index.put("h3", item.get("h3"));
            index.put("h6", item.get("h6"));
            index.put("h9", item.get("h9"));
            index.put("h12", item.get("h12"));
            index.put("h15", item.get("h15"));
            index.put("h18", item.get("h18"));
            index.put("h21", item.get("h21"));
            index.put("h24", item.get("h24"));
            index.put("h27", item.get("h27"));
            index.put("h30", item.get("h30"));
            index.put("h33", item.get("h33"));
            index.put("h36", item.get("h36"));
            index.put("h39", item.get("h39"));
            index.put("h42", item.get("h42"));
            index.put("h45", item.get("h45"));
            index.put("h48", item.get("h48"));
            index.put("h51", item.get("h51"));
            index.put("h54", item.get("h54"));
            index.put("h57", item.get("h57"));
            index.put("h60", item.get("h60"));
            index.put("h63", item.get("h63"));
            try{
                indexMapper.mergeIndex(index);
            } catch (PersistenceException e){
                log.warn("DB 저장 중 에러 발생 - areaNo: {}, error: {}",
                        item.get("areaNo"), e.getMessage());
            }
        });
    }
}
