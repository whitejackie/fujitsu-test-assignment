package com.liisabot.delivery.fees.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;

public class BaseFeeTest {

    @Test
    void canSetAndGetAllFields() {
        BaseFee baseFee = new BaseFee();

        Instant validFrom = Instant.parse("2026-01-01T00:00:00Z");
        Instant validTo = Instant.parse("2026-12-31T23:59:59Z");
        BigDecimal fee = new BigDecimal("4.50");

        baseFee.setId(1L);
        baseFee.setCity(City.TALLINN);
        baseFee.setVehicleType(VehicleType.CAR);
        baseFee.setBaseFee(fee);
        baseFee.setValidFrom(validFrom);
        baseFee.setValidTo(validTo);

        assertEquals(1L, baseFee.getId());
        assertEquals(City.TALLINN, baseFee.getCity());
        assertEquals(VehicleType.CAR, baseFee.getVehicleType());
        assertEquals(fee, baseFee.getBaseFee());
        assertEquals(validFrom, baseFee.getValidFrom());
        assertEquals(validTo, baseFee.getValidTo());
    }

    @Test
    void validToCanBeNull() {
        BaseFee baseFee = new BaseFee();

        baseFee.setCity(City.TALLINN);
        baseFee.setVehicleType(VehicleType.CAR);
        baseFee.setBaseFee(new BigDecimal("3.50"));
        baseFee.setValidFrom(Instant.parse("2026-01-01T00:00:00Z"));
        baseFee.setValidTo(null);

        assertNull(baseFee.getValidTo());
    }

    @Test
    void preservesBaseFeeDecimalPrecision() {
        BaseFee baseFee = new BaseFee();
        BigDecimal fee = new BigDecimal("12.75");

        baseFee.setBaseFee(fee);

        assertEquals(new BigDecimal("12.75"), baseFee.getBaseFee());
    }
}
