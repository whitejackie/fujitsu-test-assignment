package com.liisabot.delivery.fees.domain;

import com.liisabot.delivery.fees.exception.InvalidInputException;

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

    /**
     * Converts a string input to a City enum value.
     *
     * @param input the input string representing the city
     * @return the corresponding City enum value
     * @throws InvalidInputException if the input does not match any allowed city
     */
    public static City fromInput(String input) {
        for (City city : values()) {
            if (city.name().equalsIgnoreCase(input) || city.displayName.equalsIgnoreCase(input)) {
                return city;
            }
        }
        throw new InvalidInputException("Unknown city: " + input + ". Supported: Tallinn, Tartu, Pärnu");
    }
}
