package com.portfolio.weather.api.service;

import com.portfolio.weather.api.mapper.WeatherMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {
    
    private final WeatherMapper weatherMapper;
    public List<Map<String, Object>> getShortTermForecast(String nx, String ny) {
        return weatherMapper.selectLatestShortTermForecast(nx, ny);
    }

    public List<Map<String, Object>> getMidTermForecast(String regionId) {
        return weatherMapper.selectLatestMidTermForecast(regionId);
    }

    public List<Map<String, Object>> getWeatherWarnings(String regionId) {
        return weatherMapper.selectLatestWarning(regionId);
    }

    public List<Map<String, Object>> getIndex(String areaNo) {
        return weatherMapper.selectIndex(areaNo);
    }
}
