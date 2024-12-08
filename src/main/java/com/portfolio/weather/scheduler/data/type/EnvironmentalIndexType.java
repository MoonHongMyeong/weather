package com.portfolio.weather.scheduler.data.type;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum EnvironmentalIndexType {
    UV("자외선", "A07_2", "Ultraviolet"),
    AD("대기정체", "A09", "AirDiffusion");

    private String description;
    private String code;
    private String fullName;

    EnvironmentalIndexType(String description, String code, String fullName){
        this.description = description;
        this.code = code;
        this.fullName = fullName;
    }

    public static EnvironmentalIndexType fromCode(String code){
        for (EnvironmentalIndexType type : EnvironmentalIndexType.values()){
            if(type.code.equals(code)){
                return type;
            }
        }
        return null;
    }
}
