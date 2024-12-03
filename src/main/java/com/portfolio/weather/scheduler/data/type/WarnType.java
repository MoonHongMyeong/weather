package com.portfolio.weather.scheduler.data.type;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum WarnType {
    W("강풍"),
    R("호우"),
    C("한파"),
    D("건조"),
    O("해일"),
    N("지진해일"),
    V("풍랑"),
    T("태풍"),
    S("대설"),
    Y("황사"),
    H("폭염"),
    F("안개");

    private String description;

    WarnType(String description){
        this.description = description;
    }
}
