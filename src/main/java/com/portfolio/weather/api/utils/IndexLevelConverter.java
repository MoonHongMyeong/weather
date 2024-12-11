package com.portfolio.weather.api.utils;

import com.portfolio.weather.api.data.type.index.ADLevel;
import com.portfolio.weather.api.data.type.index.UVLevel;

public interface IndexLevelConverter {
    static String convertToDescription(String type, int value) {
        return switch (type) {
            case "UV" -> UVLevel.fromValue(value).getDescription();
            case "AD" -> ADLevel.fromValue(value).getDescription();
            default -> String.valueOf(value);
        };
    }
}