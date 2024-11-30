package com.portfolio.weather.scheduler.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.weather.scheduler.data.type.ApiResponseCode;
import com.portfolio.weather.scheduler.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiResponseParser {
    private final ObjectMapper objectMapper;

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> parseResponse(ResponseEntity<String> response) {
        try {
            // 1. JSON을 Map으로 변환
            Map<String, Object> responseMap = objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<Map<String, Object>>() {}
            );

            // 2. 헤더 검증
            Map<String, Object> header = (Map<String, Object>)
                    ((Map<String, Object>) responseMap.get("response")).get("header");

            String resultCode = (String) header.get("resultCode");
            if (!ApiResponseCode.NORMAL_SERVICE.getStatusCode().equals(resultCode)) {
                throw new ApiException("API 응답 에러: " + resultCode);
            }

            // 3. 아이템 리스트 추출
            Map<String, Object> body = (Map<String, Object>)
                    ((Map<String, Object>) responseMap.get("response")).get("body");
            Map<String, Object> items = (Map<String, Object>) body.get("items");
            return (List<Map<String, Object>>) items.get("item");

        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 중 에러 발생: {}", e.getMessage(), e);
            throw new ApiException("JSON 파싱 실패", e);
        }
    }
} 