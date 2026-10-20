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
 * A weather-related surcharge (or ban) for one vehicle type.
 * Numeric rules (temperature, wind) use a min/max range; phenomenon rules use a text fragment.
 * A "forbidden" rule has no fee: it blocks the vehicle type instead.
 */
@Entity
@Table (name = "extra_fee_rule")
@Getter @Setter @NoArgsConstructor 
public class ExtraFeeRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated (EnumType.STRING)
    @Column (nullable = false)
    private RuleCategory category;

    @Enumerated (EnumType.STRING)
    @Column (name="vehicle_type", nullable = false)
    private VehicleType vehicleType;

    private Double minValue;
    private boolean minInclusive;
    private Double maxValue;
    private boolean maxInclusive;

    private String matchText;

    @Column(precision = 6, scale = 2)
    private BigDecimal fee;

    private boolean forbidden;

    @Column(name = "valid_from", nullable = false)
    private Instant validFrom;

    @Column(name="valid_to")
    private Instant validTo;

    /** Checks if the measured value lies inside this rule's range. */
    public boolean matchesNumber(double value) {
        boolean aboveMin = minValue == null || (minInclusive ? value >= minValue : value > minValue);
        boolean belowMax = maxValue == null || (maxInclusive ? value <= maxValue : value < maxValue);
        return aboveMin && belowMax;
    }

    /** Checks if the rule matches a given phenomenon. */
    public boolean matchesPhenomenon(String phenomenon) {
        return matchText != null && phenomenon != null && phenomenon.toLowerCase().contains(matchText.toLowerCase());
    }
}
