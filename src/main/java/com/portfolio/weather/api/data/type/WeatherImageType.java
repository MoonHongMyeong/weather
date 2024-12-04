package com.portfolio.weather.api.data.type;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum WeatherImageType {
    SATELLITE("위성영상"),
    RADAR("레이더영상"),
    LIGHTNING("낙뢰분포도");
    private String description;

    WeatherImageType(String description){
        this.description = description;
    }
}
