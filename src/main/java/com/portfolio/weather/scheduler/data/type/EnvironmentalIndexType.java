package com.portfolio.weather.scheduler.data.type;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum EnvironmentalIndexType {
    A07_2("자외선", "UV", "Ultraviolet"),
    A09("대기정체", "AD", "AirDiffusion");

    private String description;
    private String name;
    private String fullName;

    EnvironmentalIndexType(String description, String name, String fullName){
        this.description = description;
        this.name = name;
        this.fullName = fullName;
    }
}
