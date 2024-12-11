package com.portfolio.weather.api.data.type.mid;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum PreType {
    WB00("강수없음"),
    WB09("비"),
    WB11("비/눈"),
    WB12("눈"),
    WB13("눈/비");

    private String description;

    PreType(String description){
        this.description = description;
    }
}
