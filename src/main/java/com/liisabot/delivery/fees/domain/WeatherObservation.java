package com.liisabot.delivery.fees.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a weather observation entity.
 */
@Entity 
@Table(name = "weather_observations",
    uniqueConstraints = @UniqueConstraint(columnNames = {"wmo_code", "observation_time"}),
    indexes = @Index(columnList = "wmo_code, observation_time"))
@Getter @Setter @NoArgsConstructor
public class WeatherObservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "station_name", nullable = false)
    private String stationName;

    @Column(name = "wmo_code", nullable = false)
    private String wmoCode;
    
    @Column(name = "air_temperature")
    private Double airTemperature;

    @Column(name="wind_speed")
    private Double windSpeed;

    @Column(name = "phenomenon")
    private String phenomenon;

    @Column(name = "observation_time", nullable = false)
    private Instant observationTime;
}
