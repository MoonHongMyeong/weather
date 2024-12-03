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
        assertThat(lines[1].trim()).startsWith("# REG_ID TM_FC        TM_EF        MOD STN C SKY  PRE  CONF WF    RN_ST");

        System.out.println("응답 데이터:\n" + responseBody);
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

        // 3. 응답 형식 확인
        assertThat(responseBody).contains("#START7777");
        assertThat(responseBody).contains("#7777END");
        
        // 4. 헤더 라인 확인
        String[] lines = responseBody.split("\n");
        assertThat(lines[1].trim()).startsWith("# REG_ID TM_FC        TM_EF        MOD STN C MIN MAX MIN_L MIN_H MAX_L MAX_H");

        System.out.println("응답 데이터:\n" + responseBody);
    }
} 