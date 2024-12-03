package com.portfolio.weather.scheduler.data;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum Location {
    DAEJEON(133, "11C20401", "11C20000");

    private int stationId;
    private String regionId;
    private String upperRegionId;

    Location(int stationId, String regionId, String upperRegionId){
        this.stationId = stationId;
        this.regionId = regionId;
        this.upperRegionId = upperRegionId;
    }
}
