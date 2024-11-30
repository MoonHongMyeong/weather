package com.portfolio.weather.scheduler.service;

import com.portfolio.weather.scheduler.data.type.FileType;
import com.portfolio.weather.scheduler.data.type.SHRT;
import com.portfolio.weather.scheduler.exception.ApiException;
import com.portfolio.weather.scheduler.mapper.VillageForecastMapper;
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
public class VillageFcstInfoService {
    private final VillageForecastMapper vfMapper;
    private final RestTemplate restTemplate;
    private final ApiResponseParser apiResponseParser;

    @Value("${kweather.api.service-key.decoding-key}")
    private String serviceKey;
    @Value("${kweather.service.VilageFcstInfoService_2.getFcstVersion}")
    private String getFcstVersionUrl;
    @Value("${kweather.service.VilageFcstInfoService_2.getVillageFcst}")
    private String getVillageFcstUrl;

    public void fetchAndSaveShrt(String nx, String ny) {
        int SHRT_ROWS = 14;
        // 1. API 호출
        LocalDateTime now = LocalDateTime.now();
        int numOfRows = SHRT_ROWS * 3; // 3시간 후까지 조회
        String apiUrl = getVillageFcstUrl +
                "?serviceKey=" + serviceKey +
                "&numOfRows=" + numOfRows +
                "&pageNo=1" +
                "&dataType=JSON" +
                "&base_date=" + BaseDateTimeUtil.getBaseDate(now) +
                "&base_time=" + BaseDateTimeUtil.getBaseTime(now) +
                "&nx=" + nx +
                "&ny=" + ny;
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        // 2. 응답 파싱
        List<Map<String, Object>> itemList = apiResponseParser.parseResponse(response);

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

    public boolean isLatestVersion(FileType fileType) {
        try {
            // 1. DB에서 최신 버전 조회
            Map<String, Object> latestVersion = vfMapper.getLatestVersionByFileType(fileType.name());

            // 2. API에서 현재 버전 조회
            String apiUrl = getFcstVersionUrl +
                    "?serviceKey=" + serviceKey +
                    "&pageNo=1" +
                    "&numOfRows=10" +
                    "&dataType=JSON" +
                    "&basedatetime=" + BaseDateTimeUtil.getBaseDateTime(LocalDateTime.now()) +
                    "&ftype=" + fileType.name();
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

            // 3. 응답 파싱
            List<Map<String, Object>> itemList = apiResponseParser.parseResponse(response);

            // 4. 버전 비교 및 업데이트
            String dbVersion = (String) latestVersion.get("version");
            String apiVersion = (String) itemList.get(0).get("version");

            if (apiVersion.compareTo(dbVersion) > 0) {
                log.info("새로운 버전이 있습니다. DB: {}, API: {}", dbVersion, apiVersion);
                
                Map<String, Object> newVersion = new HashMap<>();
                newVersion.put("version", apiVersion);
                newVersion.put("fileType", fileType.name());

                vfMapper.mergeLatestVersion(newVersion);
                log.info("새로운 버전 정보가 저장되었습니다. version: {}", apiVersion);
            
                return true;
            }

            log.info("최신 버전입니다. version: {}", dbVersion);
            return false;

        } catch (Exception e) {
            log.error("버전 체크 중 에러 발생: {}", e.getMessage(), e);
            throw new ApiException("버전 체크 실패", e);
        }
    }
}
