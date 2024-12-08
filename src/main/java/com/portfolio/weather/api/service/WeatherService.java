package com.portfolio.weather.api.service;

import com.portfolio.weather.api.data.type.mid.PreType;
import com.portfolio.weather.api.data.type.mid.SkyType;
import com.portfolio.weather.api.mapper.WeatherMapper;
import com.portfolio.weather.api.utils.IndexLevelConverter;
import com.portfolio.weather.scheduler.data.type.EnvironmentalIndexType;
import com.portfolio.weather.scheduler.data.type.SHRT;
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

    public List<Map<String, Object>> getMidTermForecast(int stationId) {
        List<Map<String, Object>> forecasts = weatherMapper.selectLatestMidTermForecast(stationId);

        forecasts.forEach(forecast -> {
            forecast.put("SKY", SkyType.valueOf((String) forecast.get("SKY")).getDescription());
            forecast.put("PRE", PreType.valueOf((String) forecast.get("PRE")).getDescription());
        });

        return forecasts;
    }

    public List<Map<String, Object>> getWeatherWarnings(int stationId) {
        return weatherMapper.selectLatestWarning(stationId);
    }

    public List<Map<String, Object>> getIndex(String areaNo) {
        List<Map<String, Object>> indexes = weatherMapper.selectIndex(areaNo);

        indexes.forEach(index -> {
            EnvironmentalIndexType type = EnvironmentalIndexType.valueOf((String) index.get("CODE"));
            index.put("CODE", type.getDescription());

            String fcstValue = (String) index.get("FCST_VALUE");
            int value = Integer.parseInt(fcstValue);
            
            String levelDescription = IndexLevelConverter.convertToDescription(type.name(), value);
            index.put("FCST_VALUE", levelDescription);
        });

        return indexes;
    }
}
