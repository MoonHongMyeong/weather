package com.portfolio.weather.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.weather.scheduler.data.type.ApiResponseCode;
import com.portfolio.weather.scheduler.data.type.FileType;
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
import static org.junit.jupiter.api.Assertions.fail;


@DisplayName("기상청 단기예보 조회서비스 API 테스트")
public class VillageFcstInfoApiSchemaTest {
    @Test
    @DisplayName("버전 조회 테스트 - 메시지 명세 확인")
    void getVersionFromRealApi() {
        RestTemplate restTemplate = new RestTemplate();
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("test.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String serviceKey = properties.getProperty("kweather.api.service-key.decoding-key");
        String getFcstVersionUrl = properties.getProperty("kweather.service.VilageFcstInfoService_2.getFcstVersion");

        // given
        FileType ft = FileType.ODAM;
        String apiUrl = getFcstVersionUrl +
                "?serviceKey=" + serviceKey +
                "&pageNo=1" +
                "&numOfRows=1" +
                "&dataType=JSON" +
                "&basedatetime=" + BaseDateTimeUtil.getBaseDateTime(LocalDateTime.now()) +
                "&ftype=" + ft.name();
        System.out.println("apiUrl : " + apiUrl);

        // when
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        // then
        // 1. HTTP 상태 코드 확인
        assertThat(response.getStatusCode().is2xxSuccessful())
                .withFailMessage("API 호출 실패: HTTP Status %s", response.getStatusCode())
                .isTrue();

        // 2. 응답 본문 존재 확인
        String responseBody = response.getBody();
        assertThat(response.getBody()).isNotNull();
        Map<String, Object> responseMap = parseResponse(responseBody);

        // 00 코드가 아니면 Test를 진행하지 않기 위한 validationCheck
        validateResultCode(responseMap);

        // response 구조 검증
        assertThat(responseMap).containsKey("response");
        Map<String, Object> responseBodyMap = (Map<String, Object>) responseMap.get("response");

        // header 검증
        // response header 명세
        Map<String, Object> header = (Map<String, Object>) ((Map<String, Object>) responseMap.get("response")).get("header");
        assertThat(header.get("resultCode")).isEqualTo("00");
        assertThat(header.get("resultMsg")).isEqualTo(ApiResponseCode.NORMAL_SERVICE.name());

        // body 검증
        Map<String, Object> body = (Map<String, Object>) responseBodyMap.get("body");
        // response body 명세
        assertThat(body.containsKey("dataType"));
        assertThat(body.containsKey("items"));
        assertThat(body.containsKey("pageNo"));
        assertThat(body.containsKey("numOfRows"));
        assertThat(body.containsKey("totalCount"));


        Map<String, Object> items = (Map<String, Object>) body.get("items");
        List<Map<String, Object>> itemList = (List<Map<String, Object>>) items.get("item");
        assertThat(itemList).isNotEmpty();

        Map<String, Object> firstItem = itemList.get(0);
        // items 명세
        assertThat(firstItem).containsKey("version");
        assertThat(firstItem).containsKey("filetype");

        // item 명세
        String version = (String) firstItem.get("version");
        String fileType = (String) firstItem.get("filetype");
        assertThat(version).matches("\\d{14}"); // yyyyMMddHHmm 형식 검증
        assertThat(fileType).isEqualTo(ft.name());

        System.out.println(responseMap.get("response"));
        System.out.println("최신 버전: " + version);


    }

    @Test
    @DisplayName("단기예보 조회 테스트 - 메시지 명세 확인")
    void getShrtFromRealApi() {
        RestTemplate restTemplate = new RestTemplate();
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("test.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String serviceKey = properties.getProperty("kweather.api.service-key.decoding-key");
        String getVillageFcstUrl = properties.getProperty("kweather.service.VilageFcstInfoService_2.getVillageFcst");
        String nx = "67";
        String ny = "101";
        int numOfRows = 14;

        LocalDateTime now = LocalDateTime.now();
        String apiUrl = getVillageFcstUrl +
                "?serviceKey=" + serviceKey +
                "&pageNo=1" +
                "&numOfRows=" + numOfRows +
                "&dataType=JSON" +
                "&base_date=" + BaseDateTimeUtil.getBaseDate(now) +
                "&base_time=" + BaseDateTimeUtil.getBaseTimeSHRT(now) +
                "&nx=" + nx +
                "&ny=" + ny;

        // when
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        // 1. HTTP 상태 코드 확인
        assertThat(response.getStatusCode().is2xxSuccessful())
                .withFailMessage("API 호출 실패: HTTP Status %s", response.getStatusCode())
                .isTrue();

        // 2. 응답 본문 존재 확인
        String responseBody = response.getBody();
        assertThat(response.getBody()).isNotNull();
        Map<String, Object> responseMap = parseResponse(responseBody);

        // 00 코드가 아니면 Test를 진행하지 않기 위한 validationCheck
        validateResultCode(responseMap);

        // response 구조 검증
        assertThat(responseMap).containsKey("response");
        Map<String, Object> responseBodyMap = (Map<String, Object>) responseMap.get("response");

        // header 검증
        Map<String, Object> header = (Map<String, Object>) ((Map<String, Object>) responseMap.get("response")).get("header");
        assertThat(header.get("resultCode")).isEqualTo("00");
        assertThat(header.get("resultMsg")).isEqualTo(ApiResponseCode.NORMAL_SERVICE.name());

        // body 검증
        Map<String, Object> body = (Map<String, Object>) responseBodyMap.get("body");
        assertThat(body.containsKey("dataType"));
        assertThat(body.containsKey("items"));
        assertThat(body.containsKey("pageNo"));
        assertThat(body.containsKey("numOfRows"));
        assertThat(body.containsKey("totalCount"));

        // items 검증
        Map<String, Object> items = (Map<String, Object>) body.get("items");
        List<Map<String, Object>> itemList = (List<Map<String, Object>>) items.get("item");
        assertThat(itemList).isNotEmpty();

        // 첫 번째 예보 아이템 검증
        Map<String, Object> firstItem = itemList.get(0);
        assertThat(firstItem).containsKey("baseDate");
        assertThat(firstItem).containsKey("baseTime");
        assertThat(firstItem).containsKey("category");
        assertThat(firstItem).containsKey("fcstDate");
        assertThat(firstItem).containsKey("fcstTime");
        assertThat(firstItem).containsKey("fcstValue");
        assertThat(firstItem).containsKey("nx");
        assertThat(firstItem).containsKey("ny");

        // 데이터 형식 검증
        String baseDate = (String) firstItem.get("baseDate");
        String baseTime = (String) firstItem.get("baseTime");
        String fcstDate = (String) firstItem.get("fcstDate");
        String fcstTime = (String) firstItem.get("fcstTime");

        assertThat(baseDate).matches("\\d{8}"); // yyyyMMdd 형식
        assertThat(baseTime).matches("\\d{4}"); // HHmm 형식
        assertThat(fcstDate).matches("\\d{8}"); // yyyyMMdd 형식
        assertThat(fcstTime).matches("\\d{4}"); // HHmm 형식
        assertThat(firstItem.get("nx")).isEqualTo(Integer.parseInt(nx));
        assertThat(firstItem.get("ny")).isEqualTo(Integer.parseInt(ny));

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
            fail(String.format("API 응답 오류: resultCode=%s, resultMsg=%s", 
                header.get("resultCode"), 
                header.get("resultMsg")));
        }
    }
}
