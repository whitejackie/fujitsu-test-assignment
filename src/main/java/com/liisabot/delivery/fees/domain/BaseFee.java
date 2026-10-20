package com.liisabot.delivery.fees.domain;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a base fee for delivery services.
 */
@Entity
@Table(name = "base_fee")
@Getter @Setter @NoArgsConstructor
public class BaseFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="city", nullable = false)
    private City city;

    @Enumerated(EnumType.STRING)
    @Column (name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @Column (name="base_fee", precision = 6, scale = 2, nullable = false)
    private BigDecimal baseFee;

    @Column (name="valid_from", nullable = false)
    private Instant validFrom;

    @Column (name="valid_to")
    private Instant validTo;
}
