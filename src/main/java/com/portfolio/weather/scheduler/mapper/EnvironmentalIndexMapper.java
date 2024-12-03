package com.portfolio.weather.scheduler.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface EnvironmentalIndexMapper {
    void mergeIndex(Map<String, Object> index);
}
