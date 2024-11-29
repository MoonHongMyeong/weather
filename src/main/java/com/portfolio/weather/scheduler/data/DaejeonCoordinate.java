package com.portfolio.weather.scheduler.data;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum DaejeonCoordinate {
    X66_Y98("66", "98"),
    X66_Y99("66", "99"),
    X66_Y100("66", "100"),
    X66_Y101("66", "101"),
    X66_Y102("66", "102"),
    X66_Y103("66", "103"),
    X67_Y98("67", "98"),
    X67_Y99("67", "99"),
    X67_Y100("67", "100"),
    X67_Y101("67", "101"),
    X67_Y102("67", "102"),
    X67_Y103("67", "103"),
    X68_Y98("68", "98"),
    X68_Y99("68", "99"),
    X68_Y100("68", "100"),
    X68_Y101("68", "101"),
    X68_Y102("68", "102"),
    X68_Y103("68", "103"),
    X69_Y98("69", "98"),
    X69_Y99("69", "99"),
    X69_Y100("69", "100"),
    X69_Y101("69", "101"),
    X69_Y102("69", "102"),
    X69_Y103("69", "103");

    private String nx;
    private String ny;

    DaejeonCoordinate(String nx, String ny) {
        this.nx = nx;
        this.ny = ny;
    }

    public String getNx() {
        return nx;
    }

    public String getNy() {
        return ny;
    }
}
