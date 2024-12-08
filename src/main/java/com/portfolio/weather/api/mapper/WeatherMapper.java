package com.portfolio.weather.api.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface WeatherMapper {

    List<Map<String, Object>> selectLatestShortTermForecast(@Param("nx") String nx, @Param("ny") String ny);

    List<Map<String, Object>> selectLatestMidTermForecast(@Param("stationId") int stationId);

    List<Map<String, Object>> selectLatestWarning(@Param("stationId") int stationId);
    
    List<Map<String, Object>> selectIndex(@Param("areaNo") String areaNo);

}