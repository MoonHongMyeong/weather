package com.portfolio.weather.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.weather.scheduler.data.type.EnvironmentalIndexType;
import com.portfolio.weather.scheduler.utils.BaseDateTimeUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("환경지수 API 스키마 테스트")
class EnvironmentalIndexApiSchemaTest {

    @Test
    @DisplayName("자외선지수 조회 테스트 - 메시지 명세 확인")
    void getUvIndexFromRealApi() {
        RestTemplate restTemplate = new RestTemplate();
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("test.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String AUTH_KEY = properties.getProperty("kweather.api-hub.auth-key");
        String UV_INDEX_URL = properties.getProperty("kweather.service.livingWeatherIndexService.UVIndex");
        String areaNo = "3000000000";  // 대전

        String apiUrl = UV_INDEX_URL +
                "?authKey=" + AUTH_KEY +
                "&areaNo=" + areaNo +
                "&time=" + BaseDateTimeUtil.getTimeForEnvironmentalIndex(LocalDateTime.now()) +
                "&pageNo=1&numOfRows=10&dataType=JSON";
        System.out.println(apiUrl);

        // when
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        // 1. HTTP 상태 코드 확인
        assertThat(response.getStatusCode().is2xxSuccessful())
                .withFailMessage("API 호출 실패: HTTP Status %s", response.getStatusCode())
                .isTrue();

        // 2. 응답 본문 존재 확인
        String responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        Map<String, Object> responseMap = parseResponse(responseBody);

        // 00 코드가 아니면 Test를 진행하지 않기 위한 validationCheck
        validateResultCode(responseMap);

        // response 구조 검증
        assertThat(responseMap).containsKey("response");
        Map<String, Object> responseBodyMap = (Map<String, Object>) responseMap.get("response");

        // header 검증
        Map<String, Object> header = (Map<String, Object>) responseBodyMap.get("header");
        assertThat(header.get("resultCode")).isEqualTo("00");
        assertThat(header.get("resultMsg")).isEqualTo("NORMAL_SERVICE");

        // body 검증
        Map<String, Object> body = (Map<String, Object>) responseBodyMap.get("body");
        
        // body 필수 필드 검증
        assertThat(body)
                .containsKey("dataType")
                .containsKey("items")
                .containsKey("pageNo")
                .containsKey("numOfRows")
                .containsKey("totalCount");

        // body 값 타입 검증
        assertThat(body.get("dataType")).isEqualTo("JSON");
        assertThat(body.get("pageNo")).isEqualTo(1);
        assertThat(body.get("numOfRows")).isEqualTo(10);
        assertThat(body.get("totalCount")).isInstanceOf(Integer.class);

        // items 검증
        Map<String, Object> items = (Map<String, Object>) body.get("items");
        List<Map<String, Object>> itemList = (List<Map<String, Object>>) items.get("item");
        assertThat(itemList).isNotEmpty();

        // 첫 번째 아이템 검증
        Map<String, Object> firstItem = itemList.get(0);
        
        // 필수 필드 존재 검증
        assertThat(firstItem)
                .containsKey("code")
                .containsKey("areaNo")
                .containsKey("date");

        // 데이터 값 검증
        assertThat(firstItem.get("code")).isEqualTo(EnvironmentalIndexType.A07_2.name());
        assertThat(firstItem.get("areaNo")).isEqualTo(areaNo);
        assertThat((String) firstItem.get("date")).matches("\\d{10}"); // yyyyMMddHH 형식

        // 시간별 예보값 검증 (h0 ~ h54)
        for (int i = 0; i <= 54; i += 3) {
            String hourKey = "h" + i;
            assertThat(firstItem).containsKey(hourKey)
                    .withFailMessage(hourKey + " 필드가 누락되었습니다.");

            String value = String.valueOf(firstItem.get(hourKey));
            if (value != null && !value.isEmpty()) {
                // 비어있지 않은 값은 숫자 형식이어야 함
                assertThat(value)
                        .withFailMessage(hourKey + " 값이 숫자 형식이 아닙니다: " + value)
                        .matches("\\d+");
                
                // 자외선지수 값 범위 검증 (0~12)
                int indexValue = Integer.parseInt(value);
                assertThat(indexValue)
                        .withFailMessage(hourKey + " 값이 유효한 범위를 벗어났습니다: " + indexValue)
                        .isBetween(0, 12);
            }
        }

        System.out.println("응답 데이터: " + responseMap.get("response"));
        System.out.println("예보 데이터: " + firstItem);
    }

    @Test
    @DisplayName("대기정체지수 조회 테스트 - 메시지 명세 확인")
    void getAirDiffusionIndexFromRealApi() {
        RestTemplate restTemplate = new RestTemplate();
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("test.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String AUTH_KEY = properties.getProperty("kweather.api-hub.auth-key");
        String AIR_DIFFUSION_URL = properties.getProperty("kweather.service.livingWeatherIndexService.AirDiffusionIndex");
        String areaNo = "3000000000";  // 대전

        String apiUrl = AIR_DIFFUSION_URL +
                "?authKey=" + AUTH_KEY +
                "&areaNo=" + areaNo +
                "&time=" + BaseDateTimeUtil.getTimeForEnvironmentalIndex(LocalDateTime.now()) +
                "&pageNo=1&numOfRows=10&dataType=JSON";
        System.out.println(apiUrl);

        // when
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        // 1. HTTP 상태 코드 확인
        assertThat(response.getStatusCode().is2xxSuccessful())
                .withFailMessage("API 호출 실패: HTTP Status %s", response.getStatusCode())
                .isTrue();

        // 2. 응답 본문 존재 확인
        String responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        Map<String, Object> responseMap = parseResponse(responseBody);

        // 00 코드가 아니면 Test를 진행하지 않기 위한 validationCheck
        validateResultCode(responseMap);

        // response 구조 검증
        assertThat(responseMap).containsKey("response");
        Map<String, Object> responseBodyMap = (Map<String, Object>) responseMap.get("response");

        // header 검증
        Map<String, Object> header = (Map<String, Object>) responseBodyMap.get("header");
        assertThat(header.get("resultCode")).isEqualTo("00");
        assertThat(header.get("resultMsg")).isEqualTo("NORMAL_SERVICE");

        // body 검증
        Map<String, Object> body = (Map<String, Object>) responseBodyMap.get("body");
        
        // body 필수 필드 검증
        assertThat(body)
                .containsKey("dataType")
                .containsKey("items")
                .containsKey("pageNo")
                .containsKey("numOfRows")
                .containsKey("totalCount");

        // body 값 타입 검증
        assertThat(body.get("dataType")).isEqualTo("JSON");
        assertThat(body.get("pageNo")).isEqualTo(1);
        assertThat(body.get("numOfRows")).isEqualTo(10);
        assertThat(body.get("totalCount")).isInstanceOf(Integer.class);

        // items 검증
        Map<String, Object> items = (Map<String, Object>) body.get("items");
        List<Map<String, Object>> itemList = (List<Map<String, Object>>) items.get("item");
        assertThat(itemList).isNotEmpty();

        // 첫 번째 아이템 검증
        Map<String, Object> firstItem = itemList.get(0);
        
        // 필수 필드 존재 검증
        assertThat(firstItem)
                .containsKey("code")
                .containsKey("areaNo")
                .containsKey("date");

        // 데이터 값 검증
        assertThat(firstItem.get("code")).isEqualTo(EnvironmentalIndexType.A09.name());
        assertThat(firstItem.get("areaNo")).isEqualTo(areaNo);
        assertThat((String) firstItem.get("date")).matches("\\d{10}"); // yyyyMMddHH 형식

        // 시간별 예보값 검증 (h0 ~ h54)
        for (int i = 3; i <= 54; i += 3) {
            String hourKey = "h" + i;
            assertThat(firstItem).containsKey(hourKey)
                    .withFailMessage(hourKey + " 필드가 누락되었습니다.");

            String value = String.valueOf(firstItem.get(hourKey));
            if (value != null && !value.isEmpty()) {
                // 비어있지 않은 값은 숫자 형식이어야 함
                assertThat(value)
                        .withFailMessage(hourKey + " 값이 숫자 형식이 아닙니다: " + value)
                        .matches("\\d+");
                
                // 대기정체지수 값 범위 검증 (25, 50, 75, 100)
                int indexValue = Integer.parseInt(value);
                assertThat(indexValue)
                        .withFailMessage(hourKey + " 값이 유효한 범위를 벗어났습니다: " + indexValue)
                        .isIn(25, 50, 75, 100);
            }
        }

        System.out.println("응답 데이터: " + responseMap.get("response"));
        System.out.println("예보 데이터: " + firstItem);
    }

    private Map<String, Object> parseResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(responseBody, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("응답 파싱 실패: " + e.getMessage());
        }
    }

    private void validateResultCode(Map<String, Object> responseMap) {
        Map<String, Object> header = (Map<String, Object>) ((Map<String, Object>) responseMap.get("response")).get("header");
        if (!"00".equals(header.get("resultCode"))) {
            throw new RuntimeException(String.format("API 응답 오류: resultCode=%s, resultMsg=%s",
                    header.get("resultCode"),
                    header.get("resultMsg")));
        }
    }
} 