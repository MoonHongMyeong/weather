package com.portfolio.weather.api.data.type.mid;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum SkyType {
    WB01("맑음"),
    WB02("구름조금"),
    WB03("구름많음"),
    WB04("흐림");

    private String description;

    SkyType(String description){
        this.description = description;
    }
}
