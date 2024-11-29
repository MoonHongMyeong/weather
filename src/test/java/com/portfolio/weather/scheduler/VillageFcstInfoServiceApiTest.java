package com.portfolio.weather.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.weather.scheduler.data.type.ApiResponseCode;
import com.portfolio.weather.scheduler.data.type.FileType;
import com.portfolio.weather.scheduler.exception.ApiException;
import com.portfolio.weather.scheduler.utils.BaseDateTimeUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@ActiveProfiles({"local", "api"})
@DisplayName("기상청 단기예보 조회서비스 API")
public class VillageFcstInfoServiceApiTest {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${kweather.api.service-key.decoding-key}")
    private String serviceKey;

    @Value("${kweather.service.VilageFcstInfoService_2.getFcstVersion}")
    private String getFcstVersionUrl;

    @Value("${kweather.service.VilageFcstInfoService_2.getVillageFcst}")
    private String getVillageFcstUrl;
    
    @Test
    @DisplayName("버전 조회 테스트 - 메시지 명세 확인")
    void getVersionFromRealApi() {

        // given
        FileType ft = FileType.ODAM;
        String apiUrl = getFcstVersionUrl +
                "?serviceKey=" + serviceKey +
                "&pageNo=1" +
                "&numOfRows=10" +
                "&dataType=JSON" +
                "&basedatetime=" + BaseDateTimeUtil.getBaseDateTime(LocalDateTime.now()) +
                "&ftype=" + ft.name();
        System.out.println("apiUrl : " + apiUrl);

        // when
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();

        // JSON 파싱 및 검증
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<Map<String, Object>>() {}
            );

            // response 구조 검증
            assertThat(responseMap).containsKey("response");
            Map<String, Object> responseBody = (Map<String, Object>) responseMap.get("response");

            // header 검증
            Map<String, Object> header = (Map<String, Object>) responseBody.get("header");
            // response header 명세
            assertThat(header.get("resultCode")).isEqualTo("00");
            assertThat(header.get("resultMsg")).isEqualTo(ApiResponseCode.NORMAL_SERVICE.name());

            // body 검증
            Map<String, Object> body = (Map<String, Object>) responseBody.get("body");
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

        } catch (Exception e) {
            fail("JSON 파싱 실패: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("단기예보 조회 테스트 - 메시지 명세 확인")
    void getVillageFcstFromRealApi() {
        // given
        String nx = "67";
        String ny = "101";

        LocalDateTime now = LocalDateTime.now();
        String apiUrl = getVillageFcstUrl +
                "?serviceKey=" + serviceKey +
                "&numOfRows=10" +
                "&pageNo=1" +
                "&dataType=JSON" +
                "&base_date=" + BaseDateTimeUtil.getBaseDate(now) +
                "&base_time=" + BaseDateTimeUtil.getBaseTime(now) +
                "&nx=" + nx +
                "&ny=" + ny;
        System.out.println("apiUrl : " + apiUrl);
        // when

        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
        System.out.println("response : " + response);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<Map<String, Object>>() {}
            );
            System.out.println(responseMap.get("response"));
        } catch (JsonProcessingException e) {
            throw new ApiException("JSON 파싱 실패", e);
        }




    }
}
