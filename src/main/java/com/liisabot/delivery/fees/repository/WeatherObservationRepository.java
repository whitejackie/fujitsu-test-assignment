package com.liisabot.delivery.fees.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.liisabot.delivery.fees.domain.WeatherObservation;

/**
 * Repository interface for managing WeatherObservation entities.
 */
public interface WeatherObservationRepository extends JpaRepository<WeatherObservation, Long> {
    boolean existsByWmoCodeAndObservationTime(String wmoCode, Instant observationTime);

    /**
     * Finds the newest observation of a station at or before the given moment.
     */
    Optional<WeatherObservation> findFirstByWmoCodeAndObservationTimeLessThanEqualOrderByObservationTimeDesc(
            String wmoCode, Instant moment);
}
