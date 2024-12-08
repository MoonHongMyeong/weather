package com.portfolio.weather.api.controller;

import com.portfolio.weather.api.service.ImageProxyService;
import com.portfolio.weather.api.service.WeatherService;
import com.portfolio.weather.common.data.Location;
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

    @GetMapping("/forecast/short/{location}")
    public ResponseEntity<List<Map<String, Object>>> getShortTermForecast(
            @PathVariable(name = "location") String requestedLocation) {
        Location location = Location.valueOf(requestedLocation);
        return ResponseEntity.ok(weatherService.getShortTermForecast(location.getNx(), location.getNy()));
    }

    @GetMapping("/forecast/mid/{location}")
    public ResponseEntity<List<Map<String, Object>>> getMidTermForecast(
            @PathVariable(name = "location") String requestedLocation) {
        Location location = Location.valueOf(requestedLocation);
        return ResponseEntity.ok(weatherService.getMidTermForecast(location.getStationId()));
    }

    @GetMapping("/warnings/{location}")
    public ResponseEntity<List<Map<String, Object>>> getWeatherWarnings(
            @PathVariable(name = "location") String requestedLocation) {
        Location location = Location.valueOf(requestedLocation);
        return ResponseEntity.ok(weatherService.getWeatherWarnings(location.getStationId()));
    }

    @GetMapping("/index/{location}")
    public ResponseEntity<List<Map<String, Object>>> getIndex(
            @PathVariable(name = "location") String requestedLocation) {
        Location location = Location.valueOf(requestedLocation);
        return ResponseEntity.ok(weatherService.getIndex(location.getAreaNo()));
    }

    @GetMapping(value = "/images/{type}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getWeatherImage(
            @PathVariable(name = "type") String type) {
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

    @GetMapping("/forecast/popup")
    public ResponseEntity<List<Map<String, Object>>> getPopupShortTermForecast(
            @RequestParam(name = "nx") int nx,
            @RequestParam(name = "ny") int ny) {
        return ResponseEntity.ok(weatherService.getPopupShortTermForecast(nx, ny));
    }
}
