package com.portfolio.weather.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TestMapper {
    @Select("SELECT #{i}")
    int select(int i);
}
