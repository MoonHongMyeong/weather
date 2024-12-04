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

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiResponseParser {
    private final ObjectMapper objectMapper;

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> parseResponseJson(ResponseEntity<String> response) {
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

    public List<Map<String, Object>> parseResponseText(ResponseEntity<String> response){
        List<Map<String, Object>> result = new ArrayList<>();
    
        // 줄 단위로 분리
        String[] lines = response.getBody().split("\n");

        // 응답이 너무 짧거나 시작/끝 태그만 있는 경우
        if (lines.length < 3 || 
            (lines.length == 3 && lines[0].contains("#START7777") && lines[2].contains("#7777END"))) {
            log.info("데이터가 없는 응답입니다: {}", response.getBody());
            return result;  // 빈 리스트 반환
        }

        String[] headers;

        // 헤더 추출 (두 번째 줄에서 # 제거 후 콤마로 분리)
        if ( lines[1].contains(",") ) {
            headers = lines[1].substring(1).trim()
                    // 기상특보 조회시 끝에 쓸모없는 값 제거
                    .replace(", =", "")
                    .split(",");
        }else{
            // 요청값에 값을 넣어도 헤더에 구분자 없는 경우
            headers = Arrays.stream(lines[1].substring(1).trim()
                    .split(" "))
                    .filter(header -> !header.isBlank())
                    .toArray(String[]::new);
        }


        // 데이터 행 처리 (일주일치)
        for (int i = 2; i < 9; i++) {
            String line = lines[i].trim()
                    // 중기 기온 예보 조회시 끝에 쓸모 없는 값이 들어감.
                    .replace(",=", "")
                    // 기상 특보 조회시 끝에 쓸모없는 값이 들어감.
                    .replace(", =", "");
            if (line.isEmpty() || line.contains("#7777END")) continue;

            // 콤마로 분리
            String[] values = line.split(",");

            // Map 생성 및 데이터 매핑
            Map<String, Object> row = new HashMap<>();
            for (int j = 0; j < headers.length && j < values.length; j++) {
                String value = values[j].trim();

                // 숫자인 경우 Integer로 변환
                if (value.matches("-?\\d{1,3}")) {
                    row.put(headers[j].trim(), Integer.parseInt(value));
                } else {
                    row.put(headers[j].trim(), value);
                }
            }

            result.add(row);
        }

        return result;
    }
} 