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
import java.time.format.DateTimeFormatter;
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
                "&pageNo=1&numOfRows=10&dataType=JSON";
        log.info(apiUrl);
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
        List<Map<String, Object>> itemList = apiResponseParser.parseResponseJson(response);

        if (itemList.isEmpty()) {
            log.warn("자외선지수 데이터가 없습니다. areaNo : " + areaNo);
            return;
        }

        itemList.forEach(item -> {
            Map<String, Object> index = new HashMap<>();
            for (int i = 0; i <= 48 ; i += 3) {
                LocalDateTime current = LocalDateTime.parse((String) item.get("date")
                                                            , DateTimeFormatter.ofPattern("yyyyMMddHH"))
                                                     .plusHours(i);
                index.put("code", EnvironmentalIndexType.valueOf((String) item.get("code")).getName());
                index.put("areaNo", item.get("areaNo"));
                index.put("fcst_date", current.format(DateTimeFormatter.ofPattern("yyyyMMddHH")));
                index.put("fcst_value", item.get("h"+i));
                System.out.println(index);
                try{
                    indexMapper.mergeIndex(index);
                } catch (PersistenceException e){
                    log.warn("DB 저장 중 에러 발생 - areaNo: {}, error: {}",
                            item.get("areaNo"), e.getMessage());
                }
            }
        });
    }

    public void fetchAndSaveAirDiffusionIndex(String areaNo) {
        String apiUrl = AIR_DIFFUSION_URL +
                "?authKey=" + AUTH_KEY +
                "&areaNo=" + areaNo +
                "&time=" + BaseDateTimeUtil.getTimeForEnvironmentalIndex(LocalDateTime.now()) +
                "&pageNo=1&numOfRows=10&dataType=JSON";
        log.info(apiUrl);
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
        List<Map<String, Object>> itemList = apiResponseParser.parseResponseJson(response);

        if (itemList.isEmpty()) {
            log.warn("대기정체지수 데이터가 없습니다. areaNo : " + areaNo);
            return;
        }

        itemList.forEach(item -> {
            Map<String, Object> index = new HashMap<>();
            for (int i = 0; i <= 48; i += 3) {
                LocalDateTime current = LocalDateTime.parse((String) item.get("date")
                                , DateTimeFormatter.ofPattern("yyyyMMddHH"))
                        .plusHours(i);
                index.put("code", EnvironmentalIndexType.valueOf((String) item.get("code")).getName());
                index.put("areaNo", item.get("areaNo"));
                index.put("fcst_date", current.format(DateTimeFormatter.ofPattern("yyyyMMddHH")));
                index.put("fcst_value", item.get("h"+i));
                System.out.println(i);
                System.out.println(index);
                try{
                    indexMapper.mergeIndex(index);
                } catch (PersistenceException e){
                    log.warn("DB 저장 중 에러 발생 - areaNo: {}, error: {}",
                            item.get("areaNo"), e.getMessage());
                }
            }
        });
    }
}
