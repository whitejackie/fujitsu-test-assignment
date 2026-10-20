package com.liisabot.delivery.fees.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.liisabot.delivery.fees.domain.ExtraFeeRule;
import com.liisabot.delivery.fees.domain.VehicleType;

/**
 * Database access for weather extra-fee rules.
 */
public interface ExtraFeeRuleRepository extends JpaRepository<ExtraFeeRule, Long> {
    /** All extra-fee rules for a vehicle type that are valid at the given moment. */
    @Query("""
        select e from ExtraFeeRule e
        where e.vehicleType = :vehicleType
        and e.validFrom <= :at and (e.validTo is null or e.validTo >= :at)
        """)
    List<ExtraFeeRule> findActiveExtraFeeRulesByVehicleType(VehicleType vehicleType, Instant at);
}
