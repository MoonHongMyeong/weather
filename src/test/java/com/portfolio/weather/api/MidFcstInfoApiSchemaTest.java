package com.portfolio.weather.api;

import com.portfolio.weather.scheduler.data.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("기상청 apihub 중기예보 조회서비스 API 테스트")
public class MidFcstInfoApiSchemaTest {

    @Test
    @DisplayName("중기육상예보 조회 테스트 - 메시지 명세 확인")
    void getMidLandForecastFromRealApi() {
        // given
        RestTemplate restTemplate = new RestTemplate();
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("test.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String AUTH_KEY = properties.getProperty("kweather.api-hub.auth-key");
        String LAND_FORECAST_URL = properties.getProperty("kweather.service.MidForecastService.landForecast");
        String locationCode = Location.DAEJEON.getUpperRegionId();

        String apiUrl = LAND_FORECAST_URL +
                "?authKey=" + AUTH_KEY +
                "&reg=" + locationCode +
                "&tmfc=0&disp=1&help=0";
        System.out.println(apiUrl);

        // when
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        // then
        // 1. HTTP 상태 코드 확인
        assertThat(response.getStatusCode().is2xxSuccessful())
                .withFailMessage("API 호출 실패: HTTP Status %s", response.getStatusCode())
                .isTrue();

        // 2. 응답 본문 존재 확인
        String responseBody = response.getBody();
        assertThat(responseBody).isNotNull();

        // 3. 응답 형식 확인
        assertThat(responseBody).contains("#START7777");
        assertThat(responseBody).contains("#7777END");
        
        // 4. 헤더 라인 확인
        String[] lines = responseBody.split("\n");
        if (lines.length > 1) {  // 데이터가 있는 경우에만 검증
            String headerLine = lines[1].trim();
            assertThat(headerLine).startsWith("# REG_ID TM_FC        TM_EF        MOD STN C SKY  PRE  CONF WF    RN_ST");
            
            // 5. 데이터 라인이 있는 경우 형식 검증
            if (lines.length > 2 && !lines[2].contains("#7777END")) {
                String[] fields = lines[2].trim().split(",");
                assertThat(fields).hasSizeGreaterThanOrEqualTo(11); // 최소 11개 필드 (마지막 =제외)
                
                // 필드 형식 검증
                assertThat(fields[0].trim()).matches(locationCode); // REG_ID
                assertThat(fields[1].trim()).matches("\\d{12}"); // TM_FC
                assertThat(fields[2].trim()).matches("\\d{12}"); // TM_EF
                assertThat(fields[3].trim()).matches("A\\d{2}"); // MOD
                assertThat(fields[4].trim()).matches("\\d+"); // STN
                assertThat(fields[5].trim()).matches("\\d"); // C
                assertThat(fields[6].trim()).matches("WB\\d{2}"); // SKY
                assertThat(fields[7].trim()).matches("WB\\d{2}"); // PRE
                // CONF는 문자열이므로 검증 생략
                // WF는 문자열이므로 검증 생략
                assertThat(fields[10].trim().replace("=", "")).matches("\\d+"); // RN_ST
            }
        }

        System.out.println("응답 데이터:\n" + responseBody);
        
        // 6. 데이터가 없는 경우도 정상 응답으로 처리
        if (lines.length <= 3) {
            System.out.println("중기육상예보 데이터가 없습니다.");
        }
    }

    @Test
    @DisplayName("중기기온예보 조회 테스트 - 메시지 명세 확인")
    void getMidTempForecastFromRealApi() {
        // given
        RestTemplate restTemplate = new RestTemplate();
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("test.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String AUTH_KEY = properties.getProperty("kweather.api-hub.auth-key");
        String TEMP_FORECAST_URL = properties.getProperty("kweather.service.MidForecastService.tempForecast");
        String locationCode = Location.DAEJEON.getRegionId();

        String apiUrl = TEMP_FORECAST_URL +
                "?authKey=" + AUTH_KEY +
                "&reg=" + locationCode +
                "&tmfc=0&disp=1&help=0";
        System.out.println(apiUrl);

        // when
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        // then
        // 1. HTTP 상태 코드 확인
        assertThat(response.getStatusCode().is2xxSuccessful())
                .withFailMessage("API 호출 실패: HTTP Status %s", response.getStatusCode())
                .isTrue();

        // 2. 응답 본문 존재 확인
        String responseBody = response.getBody();
        assertThat(responseBody).isNotNull();

        // 4. 헤더 라인 확인
        String[] lines = responseBody.split("\n");
        if (lines.length > 1) {  // 데이터가 있는 경우에만 검증
            String headerLine = lines[1].trim();
            assertThat(headerLine).startsWith("# REG_ID TM_FC        TM_EF        MOD STN C MIN MAX MIN_L MIN_H MAX_L MAX_H");
            
            // 5. 데이터 라인이 있는 경우 형식 검증
            if (lines.length > 2 && !lines[2].contains("#7777END")) {
                String[] fields = lines[2].trim().split(",");
                assertThat(fields).hasSizeGreaterThanOrEqualTo(11); // 최소 11개 필드 (마지막 =제외)
                
                // 필드 형식 검증
                assertThat(fields[0].trim()).matches(locationCode); // REG_ID
                assertThat(fields[1].trim()).matches("\\d{12}"); // TM_FC
                assertThat(fields[2].trim()).matches("\\d{12}"); // TM_EF
                assertThat(fields[3].trim()).matches("A\\d{2}"); // MOD
                assertThat(fields[4].trim()).matches("\\d+"); // STN
                assertThat(fields[5].trim()).matches("\\d"); // C
                // 기온 관련 필드는 -999 또는 정수
                String tempPattern = "(-999|\\d+|-\\d+)";
                assertThat(fields[6].trim()).matches(tempPattern); // MIN
                assertThat(fields[7].trim()).matches(tempPattern); // MAX
                assertThat(fields[8].trim()).matches(tempPattern); // MIN_L
                assertThat(fields[9].trim()).matches(tempPattern); // MIN_H
                assertThat(fields[10].trim().replace("=", "")).matches(tempPattern); // MAX_L
                assertThat(fields[11].trim().replace("=", "")).matches(tempPattern); // MAX_H
            }
        }

        System.out.println("응답 데이터:\n" + responseBody);
        
        // 6. 데이터가 없는 경우도 정상 응답으로 처리
        if (lines.length <= 3) {
            System.out.println("중기육상예보 데이터가 없습니다.");
        }
    }
} 