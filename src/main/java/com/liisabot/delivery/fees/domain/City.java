package com.liisabot.delivery.fees.domain;

import lombok.Getter;

/**
 * Enum representing the cities for delivery fee calculation.
 */
@Getter
public enum City {
    TALLINN("Tallinn", "Tallinn-Harku", "26038"),
    TARTU("Tartu", "Tartu-Tõravere", "26242"),
    PARNU("Pärnu", "Pärnu", "41803");

    private final String displayName;
    private final String stationName;
    private final String wmoCode;

    City(String displayName, String stationName, String wmoCode) {
        this.displayName = displayName;
        this.stationName = stationName;
        this.wmoCode = wmoCode;
    }
}
