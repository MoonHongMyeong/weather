package com.portfolio.weather.scheduler.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface MidForecastMapper {
    void mergeMidLandFcst(Map<String, Object> forecast);

    void mergeMidTempFcst(Map<String, Object> forecast);
}
