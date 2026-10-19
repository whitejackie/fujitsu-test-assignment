package com.liisabot.delivery.fees.service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liisabot.delivery.fees.domain.City;
import com.liisabot.delivery.fees.domain.WeatherObservation;
import com.liisabot.delivery.fees.repository.WeatherObservationRepository;
import com.liisabot.delivery.fees.weather.WeatherFeedClient;
import com.liisabot.delivery.fees.weather.WeatherXmlParser;

import lombok.RequiredArgsConstructor;

/**
 * Service for importing the latest weather data.
 */
@Service
@RequiredArgsConstructor
public class WeatherImportService {
    private static final Set<String> WANTED_WMO_CODES = Arrays.stream(City.values())
        .map(City::getWmoCode)
        .collect(Collectors.toSet());

    private final WeatherFeedClient client;
    private final WeatherXmlParser parser;
    private final WeatherObservationRepository repository;

    /**
     * Imports the latest weather data.
     */
    @Transactional
    public int importLatestData() {
        int saved = 0;
        for (WeatherObservation observation : parser.parse(client.fetch())) {
            boolean wanted = WANTED_WMO_CODES.contains(observation.getWmoCode());
            boolean alreadyStored = repository.existsByWmoCodeAndObservationTime(observation.getWmoCode(), observation.getObservationTime());
            if (wanted && !alreadyStored) {
                repository.save(observation);
                saved++;
            }
        }
        return saved;
    }
}
