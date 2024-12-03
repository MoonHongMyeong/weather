package com.portfolio.weather.scheduler.data;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum Location {
    DAEJEON(133, "11C20401", "11C20000", "L1030100");

    private int stationId;
    private String regionId;
    private String upperRegionId;
    private String warnRegionId;

    Location(int stationId, String regionId, String upperRegionId, String warnRegionId){
        this.stationId = stationId;
        this.regionId = regionId;
        this.upperRegionId = upperRegionId;
        this.warnRegionId = warnRegionId;
    }
}
