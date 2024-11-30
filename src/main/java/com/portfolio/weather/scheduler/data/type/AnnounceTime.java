package com.portfolio.weather.scheduler.data.type;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum AnnounceTime {
    H02("0200"),
    H05("0500"),
    H08("0800"),
    H11("1100"),
    H14("1400"),
    H17("1700"),
    H20("2000"),
    H23("2300");

    private String strTime;

    AnnounceTime(String strTime){
        this.strTime = strTime;
    }

    public static AnnounceTime getPreviousByStrTime(String strTime) {
        AnnounceTime current = null;
        for (AnnounceTime time : values()) {
            if (time.getStrTime().equals(strTime)) {
                current = time;
                break;
            }
        }

        if (current == null) {
            throw new IllegalArgumentException("Invalid strTime: " + strTime);
        }

        int prevIndex = (current.ordinal() - 1 + values().length) % values().length;
        return values()[prevIndex];
    }
}
