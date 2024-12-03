package com.portfolio.weather.scheduler.data.type;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum FileType {
    ODAM("초단기실황"),
    VSRT("초단기예보");
    private String description;
    FileType(String description){
        this.description = description;
    }
}
