package com.liisabot.delivery.fees.domain;

import com.liisabot.delivery.fees.exception.InvalidInputException;

/**
 * Enum representing the types of vehicles for delivery fee calculation.
 */
public enum VehicleType {
    CAR, SCOOTER, BIKE;

    public static VehicleType fromInput(String input) {
        for (VehicleType vehicleType : values()) {
            if (vehicleType.name().equalsIgnoreCase(input)) {
                return vehicleType;
            }
        }
        throw new InvalidInputException("Unknown vehicle type: " + input + ". Supported: CAR, SCOOTER, BIKE");
    }
}
