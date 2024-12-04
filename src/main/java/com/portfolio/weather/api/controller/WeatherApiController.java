package com.portfolio.weather.api.controller;

import com.portfolio.weather.api.service.ImageProxyService;
import com.portfolio.weather.api.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
public class WeatherApiController {

    private final WeatherService weatherService;
    private final ImageProxyService imageProxyService;

    @GetMapping("/forecast/short")
    public ResponseEntity<List<Map<String, Object>>> getShortTermForecast(
            @RequestParam String nx,
            @RequestParam String ny) {
        return ResponseEntity.ok(weatherService.getShortTermForecast(nx, ny));
    }

    @GetMapping("/forecast/mid")
    public ResponseEntity<List<Map<String, Object>>> getMidTermForecast(
            @RequestParam String regionId) {
        return ResponseEntity.ok(weatherService.getMidTermForecast(regionId));
    }

    @GetMapping("/warnings")
    public ResponseEntity<List<Map<String, Object>>> getWeatherWarnings(
            @RequestParam String regionId) {
        return ResponseEntity.ok(weatherService.getWeatherWarnings(regionId));
    }

    @GetMapping("/index")
    public ResponseEntity<List<Map<String, Object>>> getIndex(
            @RequestParam String areaNo) {
        return ResponseEntity.ok(weatherService.getIndex(areaNo));
    }

    @GetMapping(value = "/images/{type}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getWeatherImage(
            @PathVariable String type) {
        try {
            byte[] imageBytes = imageProxyService.getWeatherImage(type);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .cacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES))
                    .body(imageBytes);
                    
        } catch (Exception e) {
            log.error("이미지 조회 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
