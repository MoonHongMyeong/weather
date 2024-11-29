package com.portfolio.weather.scheduler.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.weather.scheduler.data.type.ApiResponseCode;
import com.portfolio.weather.scheduler.data.type.FileType;
import com.portfolio.weather.scheduler.exception.ApiException;
import com.portfolio.weather.scheduler.mapper.VillageForecastMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class VillageFcstInfoService {
    private final VillageForecastMapper vfMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kweather.api.service-key.decoding-key}")
    private String serviceKey;
    @Value("${kweather.service.VilageFcstInfoService_2.getFcstVersion}")
    private String getFcstVersionUrl;
    @Value("${kweather.service.VilageFcstInfoService_2.getVillageFcst}")
    private String getVillageFcstUrl;
    @Value("${kweather.service.VilageFcstInfoService_2.getUltraSrtFcst}")
    private String getUltraSrtFcstUrl;
    @Value("${kweather.service.VilageFcstInfoService_2.getUltraSrtNcst}")
    private String getUltraSrtNcstUrl;

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
                    "&basedatetime=" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")) +
                    "&ftype=" + fileType.name();

            // JSON 문자열로 응답 받기
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

            // JSON을 Map으로 변환
            Map<String, Object> responseMap = objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<Map<String, Object>>() {}
            );

            // 3. 버전 비교
            String dbVersion = (String) latestVersion.get("version");
            String apiVersion = extractVersionFromResponse(responseMap);

            // 4. api 버전이 db 버전보다 크면 db update 후 true return
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

        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 중 에러 발생: {}", e.getMessage(), e);
            throw new ApiException("JSON 파싱 실패", e);
        } catch (Exception e) {
            log.error("버전 체크 중 에러 발생: {}", e.getMessage(), e);
            throw new ApiException("버전 체크 실패", e);
        }
    }

    @SuppressWarnings("unchecked")
    private String extractVersionFromResponse(Map<String, Object> response) {
        try {
            Map<String, Object> header = (Map<String, Object>)
                    ((Map<String, Object>) response.get("response")).get("header");

            String resultCode = (String) header.get("resultCode");
            if (!ApiResponseCode.NORMAL_SERVICE.getStatusCode().equals(resultCode)) {
                throw new ApiException("API 응답 에러: " + resultCode);
            }

            Map<String, Object> body = (Map<String, Object>)
                    ((Map<String, Object>) response.get("response")).get("body");
            Map<String, Object> items = (Map<String, Object>) body.get("items");
            List<Map<String, Object>> itemList = (List<Map<String, Object>>) items.get("item");

            if (itemList.isEmpty()) {
                throw new ApiException("버전 정보가 없습니다");
            }

            return (String) itemList.get(0).get("version");

        } catch (Exception e) {
            throw new ApiException("버전 정보 추출 실패", e);
        }
    }

}
