package com.portfolio.weather.api;

import com.portfolio.weather.scheduler.data.Location;
import com.portfolio.weather.scheduler.data.type.WarnType;
import com.portfolio.weather.scheduler.utils.BaseDateTimeUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("기상청 apihub 기상특보 조회서비스 API 테스트")
public class WthrWrnApiSchemaTest {

    @Test
    @DisplayName("기상특보 조회 테스트 - 메시지 명세 확인")
    void getWeatherWarningFromRealApi() {
        // given
        RestTemplate restTemplate = new RestTemplate();
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("test.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String AUTH_KEY = properties.getProperty("kweather.api-hub.auth-key");
        String WEATHER_WARN_URL = properties.getProperty("kweather.service.weatherWarnService");
        String locationCode = Location.DAEJEON.getWarnRegionId();

        String apiUrl = WEATHER_WARN_URL +
                "?authKey=" + AUTH_KEY +
                "&reg=" + locationCode +
                "&tmfc1=" + BaseDateTimeUtil.getBaseDateTime(LocalDateTime.now()) +
                //"&tmfc1=202411280000" + // 데이터 존재하는 날짜
                "&disp=0&help=0";
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
            assertThat(headerLine).startsWith("#      TM_FC,        TM_EF,        TM_IN, STN,   REG_ID, WRN, LVL, CMD, GRD, CNT,   RPT, =");
            
            // 5. 데이터 라인이 있는 경우 형식 검증
            if (lines.length > 2 && !lines[2].contains("#7777END")) {
                String[] fields = lines[2].trim().split(",");
                assertThat(fields).hasSizeGreaterThanOrEqualTo(11);
                
                // 날짜/시간 형식 검증 (YYYYMMDDHHmm)
                assertThat(fields[0].trim()).matches("\\d{12}");      // TM_FC
                assertThat(fields[1].trim()).matches("\\d{12}");      // TM_EF
                assertThat(fields[2].trim()).matches("\\d{12}");      // TM_IN
                assertThat(fields[3].trim()).matches("\\d+");         // STN
                assertThat(fields[4].trim()).matches("[A-Z]\\d+");    // REG_ID (L로 시작하는 숫자)
                String wrnValue = fields[5].trim();
                assertThat(wrnValue)
                        .withFailMessage("WRN 값 %s이(가) WarnType에 정의되지 않았습니다.", wrnValue)
                        .isEqualTo(WarnType.valueOf(wrnValue).name());      // WRN (WarnType 에 정의되어 있어야함)
                assertThat(fields[6].trim()).matches("\\d+");         // LVL (숫자)
                assertThat(fields[7].trim()).matches("\\d+");         // CMD (숫자)
                assertThat(fields[8].trim()).matches("\\d{2}");       // GRD (2자리 숫자)
                assertThat(fields[9].trim()).matches("\\d+");         // CNT (숫자)
                assertThat(fields[10].trim()).matches("\\d+");        // RPT (숫자)
            }
        }

        System.out.println("응답 데이터:\n" + responseBody);
        
        // 6. 데이터가 없는 경우도 정상 응답으로 처리
        if (lines.length <= 3) {
            System.out.println("현재 발효 중인 기상특보가 없습니다.");
        }
    }
} 