package com.portfolio.weather.scheduler.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.Map;

@Mapper
public interface WeatherWarnMapper {
    void mergeWeatherWarn(Map<String, Object> warn);
}
