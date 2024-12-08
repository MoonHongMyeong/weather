package com.portfolio.weather.api.data.type.index;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum UVLevel {
    DANGER(11, "위험"),
    VERY_HIGH(8, 10, "매우 높음"),
    HIGH(6,7, "높음"),
    NORMAL(3,5, "보통"),
    LOW(0,2, "낮음");

    private int lowLevel;
    private int highLevel;
    private String description;
    UVLevel(int low, String description) {
        this.lowLevel = low;
        this.description = description;
    }
    UVLevel(int low, int high, String description){
        this.lowLevel = low;
        this.highLevel = high;
        this.description = description;
    }

    public static UVLevel fromValue(int value) {
        for (UVLevel level : UVLevel.values()) {
            if (level.isInRange(value)) {
                return level;
            }
        }
        return UVLevel.NORMAL;// 기본값 반환
    }

    private boolean isInRange(int value) {
        return value >= this.lowLevel && value <= this.highLevel;
    }
}
