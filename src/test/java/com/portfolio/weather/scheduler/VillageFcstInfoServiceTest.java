package com.portfolio.weather.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.weather.scheduler.data.type.FileType;
import com.portfolio.weather.scheduler.exception.ApiException;
import com.portfolio.weather.scheduler.mapper.VillageForecastMapper;
import com.portfolio.weather.scheduler.service.VillageFcstInfoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class VillageFcstInfoServiceTest {

    @Mock
    private VillageForecastMapper vfMapper;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private VillageFcstInfoService villageFcstInfoService;

    @Nested
    @DisplayName("예보 버전 체크")
    class checkForecast{
        @Test
        @DisplayName("API 버전이 DB 버전보다 높을 때 true를 반환하고 DB를 업데이트해야 한다")
        void shouldUpdateAndReturnTrueWhenNewVersionExists() throws JsonProcessingException {
            // given
            FileType fileType = FileType.ODAM;
            String dbVersion = "202403110800";
            String apiVersion = "202403111400";

            // DB 버전 조회 모킹
            Map<String, Object> dbVersionInfo = new HashMap<>();
            dbVersionInfo.put("version", dbVersion);
            when(vfMapper.getLatestVersionByFileType(fileType.name())).thenReturn(dbVersionInfo);

            // API 응답 모킹
            String mockResponseBody = createMockResponseJson(apiVersion, fileType.name());
            ResponseEntity<String> mockResponse = ResponseEntity.ok(mockResponseBody);
            when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(mockResponse);

            Map<String, Object> mockParsedResponse = createMockParsedResponse(apiVersion, fileType.name());
            when(objectMapper.readValue(eq(mockResponseBody), any(TypeReference.class)))
                    .thenReturn(mockParsedResponse);

            // when
            boolean result = villageFcstInfoService.isLatestVersion(fileType);

            // then
            assertThat(result).isTrue();
            verify(vfMapper).mergeLatestVersion(argThat(map ->
                    map.get("version").equals(apiVersion) &&
                            map.get("fileType").equals(fileType.name())
            ));
        }

        @Test
        @DisplayName("API 버전이 DB 버전과 같을 때 false를 반환해야 한다")
        void shouldReturnFalseWhenVersionsAreEqual() throws JsonProcessingException {
            // given
            FileType fileType = FileType.ODAM;
            String version = "202403111400";

            Map<String, Object> dbVersionInfo = new HashMap<>();
            dbVersionInfo.put("version", version);
            when(vfMapper.getLatestVersionByFileType(fileType.name())).thenReturn(dbVersionInfo);

            String mockResponseBody = createMockResponseJson(version, fileType.name());
            ResponseEntity<String> mockResponse = ResponseEntity.ok(mockResponseBody);
            when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(mockResponse);

            Map<String, Object> mockParsedResponse = createMockParsedResponse(version, fileType.name());
            when(objectMapper.readValue(eq(mockResponseBody), any(TypeReference.class)))
                    .thenReturn(mockParsedResponse);

            // when
            boolean result = villageFcstInfoService.isLatestVersion(fileType);

            // then
            assertThat(result).isFalse();
            verify(vfMapper, never()).mergeLatestVersion(any());
        }

        private String createMockResponseJson(String version, String fileType) {
            return String.format("""
            {
                "response": {
                    "header": {
                        "resultCode": "00",
                        "resultMsg": "NORMAL_SERVICE"
                    },
                    "body": {
                        "items": {
                            "item": [
                                {
                                    "version": "%s",
                                    "filetype": "%s"
                                }
                            ]
                        }
                    }
                }
            }
            """, version, fileType);
        }

        private Map<String, Object> createMockParsedResponse(String version, String fileType) {
            // API 응답 구조에 맞게 Map 생성
            Map<String, Object> response = new HashMap<>();
            Map<String, Object> header = new HashMap<>();
            Map<String, Object> body = new HashMap<>();
            Map<String, Object> items = new HashMap<>();
            List<Map<String, Object>> itemList = new ArrayList<>();
            Map<String, Object> item = new HashMap<>();

            item.put("version", version);
            item.put("filetype", fileType);
            itemList.add(item);
            items.put("item", itemList);
            body.put("items", items);
            header.put("resultCode", "00");

            response.put("header", header);
            response.put("body", body);

            Map<String, Object> root = new HashMap<>();
            root.put("response", response);

            return root;
        }
    }

    @Nested
    @DisplayName("단기예보 조회 및 저장")
    class FetchAndSaveShrtTest {
        
        @Test
        @DisplayName("정상적인 API 응답을 받아 DB에 저장해야 한다")
        void shouldSaveValidForecastData() throws JsonProcessingException {
            // given
            String nx = "67";
            String ny = "101";
            LocalDateTime now = LocalDateTime.now();
            
            // API 응답 모킹
            String mockResponseBody = createMockShrtResponseJson();
            ResponseEntity<String> mockResponse = ResponseEntity.ok(mockResponseBody);
            when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(mockResponse);
            
            Map<String, Object> mockParsedResponse = createMockShrtParsedResponse();
            when(objectMapper.readValue(eq(mockResponseBody), any(TypeReference.class)))
                    .thenReturn(mockParsedResponse);

            // when
            villageFcstInfoService.fetchAndSaveShrt(nx, ny);

            // then
            verify(vfMapper, times(2)).mergeShrt(argThat(forecast -> 
                forecast.get("nx").equals(nx) &&
                forecast.get("ny").equals(ny) &&
                forecast.containsKey("baseDate") &&
                forecast.containsKey("baseTime") &&
                forecast.containsKey("fcstDate") &&
                forecast.containsKey("fcstTime") &&
                forecast.containsKey("category") &&
                forecast.containsKey("fcstValue")
            ));
        }

        @Test
        @DisplayName("API 에러 응답시 예외가 발생해야 한다")
        void shouldThrowExceptionOnApiError() throws JsonProcessingException {
            // given
            String nx = "67";
            String ny = "101";
            
            String mockResponseBody = createErrorResponseJson();
            ResponseEntity<String> mockResponse = ResponseEntity.ok(mockResponseBody);
            when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(mockResponse);
            
            Map<String, Object> mockErrorResponse = createMockErrorResponse();
            when(objectMapper.readValue(eq(mockResponseBody), any(TypeReference.class)))
                    .thenReturn(mockErrorResponse);

            // when & then
            assertThatThrownBy(() -> villageFcstInfoService.fetchAndSaveShrt(nx, ny))
                    .isInstanceOf(ApiException.class)
                    .hasMessageContaining("API 응답 에러");
            
            verify(vfMapper, never()).mergeShrt(any());
        }

        private String createMockShrtResponseJson() {
            return """
                {
                    "response": {
                        "header": {
                            "resultCode": "00",
                            "resultMsg": "NORMAL_SERVICE"
                        },
                        "body": {
                            "items": {
                                "item": [
                                    {
                                        "baseDate": "20240312",
                                        "baseTime": "0500",
                                        "category": "TMP",
                                        "fcstDate": "20240312",
                                        "fcstTime": "0600",
                                        "fcstValue": "12"
                                    },
                                    {
                                        "baseDate": "20240312",
                                        "baseTime": "0500",
                                        "category": "REH",
                                        "fcstDate": "20240312",
                                        "fcstTime": "0600",
                                        "fcstValue": "85"
                                    }
                                ]
                            }
                        }
                    }
                }
                """;
        }

        private Map<String, Object> createMockShrtParsedResponse() {
            Map<String, Object> response = new HashMap<>();
            Map<String, Object> header = new HashMap<>();
            Map<String, Object> body = new HashMap<>();
            Map<String, Object> items = new HashMap<>();
            List<Map<String, Object>> itemList = new ArrayList<>();
            
            // TMP 데이터
            Map<String, Object> tmp = new HashMap<>();
            tmp.put("baseDate", "20240312");
            tmp.put("baseTime", "0500");
            tmp.put("category", "TMP");
            tmp.put("fcstDate", "20240312");
            tmp.put("fcstTime", "0600");
            tmp.put("fcstValue", "12");
            
            // REH 데이터
            Map<String, Object> reh = new HashMap<>();
            reh.put("baseDate", "20240312");
            reh.put("baseTime", "0500");
            reh.put("category", "REH");
            reh.put("fcstDate", "20240312");
            reh.put("fcstTime", "0600");
            reh.put("fcstValue", "85");
            
            itemList.add(tmp);
            itemList.add(reh);
            items.put("item", itemList);
            body.put("items", items);
            header.put("resultCode", "00");
            header.put("resultMsg", "NORMAL_SERVICE");
            
            Map<String, Object> headerBody = new HashMap<>();
            headerBody.put("header", header);
            headerBody.put("body", body);
            response.put("response", headerBody);
            
            return response;
        }

        private String createErrorResponseJson() {
            return """
                {
                    "response": {
                        "header": {
                            "resultCode": "03",
                            "resultMsg": "NO_DATA"
                        },
                        "body": null
                    }
                }
                """;
        }

        private Map<String, Object> createMockErrorResponse() {
            Map<String, Object> response = new HashMap<>();
            Map<String, Object> header = new HashMap<>();
            header.put("resultCode", "03");
            header.put("resultMsg", "NO_DATA");
            
            Map<String, Object> headerBody = new HashMap<>();
            headerBody.put("header", header);
            headerBody.put("body", null);
            response.put("response", headerBody);
            
            return response;
        }
    }


}
