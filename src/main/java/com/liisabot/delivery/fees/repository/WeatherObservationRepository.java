package com.liisabot.delivery.fees.repository;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;

import com.liisabot.delivery.fees.domain.WeatherObservation;

/**
 * Repository interface for managing WeatherObservation entities.
 */
public interface WeatherObservationRepository extends JpaRepository<WeatherObservation, Long> {
    boolean existsByWmoCodeAndObservationTime(String wmoCode, Instant observationTime);
}
