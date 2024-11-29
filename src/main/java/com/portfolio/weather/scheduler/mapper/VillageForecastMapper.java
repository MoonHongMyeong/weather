package com.portfolio.weather.scheduler.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface VillageForecastMapper {
    Map<String, Object> getLatestVersionByFileType(String fileType);
    int mergeLatestVersion(Map<String, Object> params);

    void mergeShrt(Map<String, Object> forecast);
}
