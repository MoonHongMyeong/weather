package com.portfolio.weather.common.data;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum Location {
    DAEJEON(133, "11C20401", "11C20000", "L1030100", "3000000000", "60", "100");

    private int stationId;
    private String regionId;
    private String upperRegionId;
    private String warnRegionId;
    private String areaNo;
    private String nx;
    private String ny;

    Location(int stationId, String regionId, String upperRegionId, String warnRegionId, String areaNo, String nx, String ny){
        this.stationId = stationId;
        this.regionId = regionId;
        this.upperRegionId = upperRegionId;
        this.warnRegionId = warnRegionId;
        this.areaNo = areaNo;
        this.nx = nx;
        this.ny = ny;
    }
}
