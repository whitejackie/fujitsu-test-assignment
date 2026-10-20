package com.liisabot.delivery.fees.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.liisabot.delivery.fees.exception.InvalidInputException;

public class EnumParsingTest {
    @Test 
    void cityIsParsedIgnoringCaseAndAccents() {
        assertEquals(City.TARTU, City.fromInput("tartu"));
        assertEquals(City.PARNU, City.fromInput("Pärnu"));
        assertEquals(City.PARNU, City.fromInput("PARNU"));
    }

    @Test
    void unknownCityThrowsInvalidInput() {
        assertThrows(InvalidInputException.class, () -> City.fromInput("Narva"));
    }

    @Test
    void vehicleTypeIsParsedIgnoringCase() {
        assertEquals(VehicleType.BIKE, VehicleType.fromInput("bike"));
        assertEquals(VehicleType.SCOOTER, VehicleType.fromInput("Scooter"));
    }

    @Test
    void unknownVehicleTypeThrowsInvalidInput() {
        assertThrows(InvalidInputException.class, () -> VehicleType.fromInput("Plane"));
    }
}
