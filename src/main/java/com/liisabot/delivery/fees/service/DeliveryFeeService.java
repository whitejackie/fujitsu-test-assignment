package com.liisabot.delivery.fees.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.liisabot.delivery.fees.domain.BaseFee;
import com.liisabot.delivery.fees.domain.City;
import com.liisabot.delivery.fees.domain.ExtraFeeRule;
import com.liisabot.delivery.fees.domain.RuleCategory;
import com.liisabot.delivery.fees.domain.VehicleType;
import com.liisabot.delivery.fees.domain.WeatherObservation;
import com.liisabot.delivery.fees.dto.DeliveryFeeResponse;
import com.liisabot.delivery.fees.exception.ResourceNotFoundException;
import com.liisabot.delivery.fees.exception.VehicleForbiddenException;
import com.liisabot.delivery.fees.exception.WeatherDataNotFoundException;
import com.liisabot.delivery.fees.repository.BaseFeeRepository;
import com.liisabot.delivery.fees.repository.ExtraFeeRuleRepository;
import com.liisabot.delivery.fees.repository.WeatherObservationRepository;

@Service 
public class DeliveryFeeService {

    private final WeatherObservationRepository weatherRepository;
    private final BaseFeeRepository baseFeeRepository;
    private final ExtraFeeRuleRepository ruleRepository;

    public DeliveryFeeService(WeatherObservationRepository weatherRepository, 
        BaseFeeRepository baseFeeRepository, 
        ExtraFeeRuleRepository ruleRepository
    ) {
        this.weatherRepository = weatherRepository;
        this.baseFeeRepository = baseFeeRepository;
        this.ruleRepository = ruleRepository;
    }

    public DeliveryFeeResponse calculate(City city, VehicleType vehicleType, Instant at) {
        Instant moment = Instant.now();
        if (at != null) {
            moment = at;
        }

        WeatherObservation weather = findWeather(city, moment);
        BigDecimal baseFee = findBaseFee(city, vehicleType, moment);
        List<ExtraFeeRule> rules = ruleRepository.findActiveExtraFeeRulesByVehicleType(vehicleType, moment);

        BigDecimal total = baseFee;
        total = total.add(temperatureFee(rules, weather.getAirTemperature()));
        total = total.add(windFee(rules, weather.getWindSpeed()));
        total = total.add(phenomenonFee(rules, weather.getPhenomenon()));

        return new DeliveryFeeResponse(city.getDisplayName(), vehicleType.name(), total, weather.getObservationTime());
    }

     /** Finds the newest weather reading of the city at or before the given moment. */
    private WeatherObservation findWeather(City city, Instant moment) {
        Optional<WeatherObservation> result = weatherRepository
                .findFirstByWmoCodeAndObservationTimeLessThanEqualOrderByObservationTimeDesc(city.getWmoCode(), moment);
        if (result.isEmpty()) {
            throw new WeatherDataNotFoundException(
                    "No weather data available for " + city.getDisplayName() + " at the requested time");
        }
        return result.get();
    }

    /** Finds the base fee that was valid at the given moment. */
    private BigDecimal findBaseFee(City city, VehicleType vehicle, Instant moment) {
        List<BaseFee> fees = baseFeeRepository.findActiveBaseFees(city, vehicle, moment);
        if (fees.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No base fee defined for " + city.getDisplayName() + " / " + vehicle);
        }
        // the list is sorted newest first, so the first one is the right one
        return fees.get(0).getBaseFee();
    }

    private BigDecimal temperatureFee(List<ExtraFeeRule> rules, Double temperature) {
        if (temperature == null) {
            return BigDecimal.ZERO;
        }
        return numberFee(rules, RuleCategory.AIR_TEMPERATURE, temperature);
    }

    private BigDecimal windFee(List<ExtraFeeRule> rules, Double windSpeed) {
        if (windSpeed == null) {
            return BigDecimal.ZERO;
        }
        return numberFee(rules, RuleCategory.WIND_SPEED, windSpeed);
    }

    /**
     * Goes through the rules of one category (temperature or wind).
     * A "forbidden" rule stops everything, otherwise the highest matching fee is used.
     */
    private BigDecimal numberFee(List<ExtraFeeRule> rules, RuleCategory category, double value) {
        BigDecimal highest = BigDecimal.ZERO;
        for (ExtraFeeRule rule : rules) {
            if (rule.getCategory() == category && rule.matchesNumber(value)) {
                if (rule.isForbidden()) {
                    throw new VehicleForbiddenException();
                }
                // BigDecimal cannot use ">", so we use compareTo (result above 0 means "bigger")
                if (rule.getFee().compareTo(highest) > 0) {
                    highest = rule.getFee();
                }
            }
        }
        return highest;
    }

    /** Same idea as numberFee, but for the weather phenomenon text (snow, rain, ...). */
    private BigDecimal phenomenonFee(List<ExtraFeeRule> rules, String phenomenon) {
        BigDecimal highest = BigDecimal.ZERO;
        for (ExtraFeeRule rule : rules) {
            if (rule.getCategory() == RuleCategory.PHENOMENON && rule.matchesPhenomenon(phenomenon)) {
                if (rule.isForbidden()) {
                    throw new VehicleForbiddenException();
                }
                if (rule.getFee().compareTo(highest) > 0) {
                    highest = rule.getFee();
                }
            }
        }
        return highest;
    }
}
