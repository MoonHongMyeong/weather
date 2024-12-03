package com.portfolio.weather.scheduler.data;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum Location {
    DAEJEON(133, "11C20401");

    private int stationId;
    private String regionId;

    Location(int stationId, String regionId){
        this.stationId = stationId;
        this.regionId = regionId;
    }
}
