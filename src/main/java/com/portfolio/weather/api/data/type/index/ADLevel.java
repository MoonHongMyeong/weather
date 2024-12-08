package com.portfolio.weather.api.data.type.index;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum ADLevel {
    LOW("낮음", 100), NORMAL("보통", 75), HIGH("높음", 50), VERY_HIGH("매우 높음", 25);
    private String description;
    private int value;

    ADLevel(String description, int value){
        this.description = description;
        this.value = value;
    }

    public static ADLevel fromValue(int value){
        for (ADLevel level : ADLevel.values()) {
            if (value == level.getValue()){
                return level;
            }
        }
        return ADLevel.NORMAL;// 기본값
    }
}
