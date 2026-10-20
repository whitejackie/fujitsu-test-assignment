package com.liisabot.delivery.fees.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.liisabot.delivery.fees.domain.BaseFee;
import com.liisabot.delivery.fees.domain.City;
import com.liisabot.delivery.fees.domain.VehicleType;

/**
 * Repository interface for managing BaseFee entities.
 */
public interface BaseFeeRepository extends JpaRepository<BaseFee, Long> {
    /** Base fees valid at the given moment, newest first*/
    @Query ("""
        select b from BaseFee b
        where b.city = :city and b.vehicleType = :vehicleType
        and b.validFrom <= :at and (b.validTo is null or b.validTo >= :at)
        order by b.validFrom desc
        """)
    List<BaseFee> findActiveBaseFees(@Param ("city") City city,
                                      @Param("vehicleType") VehicleType vehicleType,
                                      @Param("at") Instant at);
}
