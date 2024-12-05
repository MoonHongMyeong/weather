package com.portfolio.weather.scheduler.service;

import com.portfolio.weather.scheduler.mapper.WeatherWarnMapper;
import com.portfolio.weather.scheduler.utils.ApiResponseParser;
import com.portfolio.weather.scheduler.utils.BaseDateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WthrWrnService {
    private final WeatherWarnMapper wwMapper;
    private final RestTemplate restTemplate;
    private final ApiResponseParser apiResponseParser;

    @Value("${kweather.api-hub.auth-key}")
    private String AUTH_KEY;
    @Value("${kweather.service.weatherWarnService}")
    private String WEATHER_WARN_URL;
    public void fetchAndSaveWeatherWarning(String warnRegionId) {
        String apiUrl = WEATHER_WARN_URL +
                "?authKey=" + AUTH_KEY +
                "&reg=" + warnRegionId +
                "&tmfc1=" + BaseDateTimeUtil.getBaseDateTime(LocalDateTime.now()) +
                "&disp=0&help=0";
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        // 2. 응답 파싱
        List<Map<String, Object>> itemList = apiResponseParser.parseResponseText(response);

        // 데이터가 없으면 종료
        if (itemList.isEmpty()) {
            log.info("기상특보 데이터가 없습니다. locationCode: {}", warnRegionId);
            return;
        }

        itemList.forEach( item -> {
            Map<String, Object> warn = new HashMap<>();
            warn.put("tm_fc", item.get("TM_FC")); // 발표시각
            warn.put("tm_ef", item.get("TM_EF")); // 발효시각
            warn.put("tm_in", item.get("TM_IN")); // 입력시각
            warn.put("stn", item.get("STN")); // 발표관서
            warn.put("reg_id", item.get("REG_ID")); // 구역코드
            warn.put("wrn", item.get("WRN")); // 특보종류
            warn.put("lvl", item.get("LVL")); // 특보수준 (1:예비, 2:주의보, 3:경보)
            warn.put("cmd", item.get("CMD")); // 특보명령 (1:발표, 2:대치, 3:해제, 4:대치해제(자동), 5:연장, 6:변경, 7:변경해제)
            warn.put("grd", item.get("GRD")); // 태풍경보시 등급 (바람:A(1,2,3), 비:B(1,2,3))
            warn.put("cnt", item.get("CNT")); // 작업상태 (4:통보완료)
            try{
                wwMapper.mergeWeatherWarn(warn);
            }catch (PersistenceException e) {
                log.warn("DB 저장 중 에러 발생 - regId: {}, error: {}",
                        item.get("regId"), e.getMessage());
            }
        });
    }
}
