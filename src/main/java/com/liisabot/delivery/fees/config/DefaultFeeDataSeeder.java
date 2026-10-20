package com.liisabot.delivery.fees.config;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.liisabot.delivery.fees.domain.BaseFee;
import com.liisabot.delivery.fees.domain.City;
import com.liisabot.delivery.fees.domain.ExtraFeeRule;
import com.liisabot.delivery.fees.domain.RuleCategory;
import com.liisabot.delivery.fees.domain.VehicleType;
import com.liisabot.delivery.fees.repository.BaseFeeRepository;
import com.liisabot.delivery.fees.repository.ExtraFeeRuleRepository;

import lombok.RequiredArgsConstructor;

/**
 * Seeds the database with default fee data if it is empty.
 */
@Component
@RequiredArgsConstructor 
public class DefaultFeeDataSeeder implements ApplicationRunner {

    private final BaseFeeRepository baseFeeRepository;
    private final ExtraFeeRuleRepository ruleRepository;

    @Override
    @Transactional 
    public void run(ApplicationArguments args) {
        if (baseFeeRepository.count() == 0) {
            seedBaseFees();
        }
        if (ruleRepository.count() == 0) {
            seedExtraFeeRules();
        }
    }

    private void seedBaseFees() {
        baseFee(City.TALLINN, VehicleType.CAR, "4.00");
        baseFee(City.TALLINN, VehicleType.SCOOTER, "3.50");
        baseFee(City.TALLINN, VehicleType.BIKE, "3.00");
        baseFee(City.TARTU, VehicleType.CAR, "3.50");
        baseFee(City.TARTU, VehicleType.SCOOTER, "3.00");
        baseFee(City.TARTU, VehicleType.BIKE, "2.50");
        baseFee(City.PARNU, VehicleType.CAR, "3.00");
        baseFee(City.PARNU, VehicleType.SCOOTER, "2.50");
        baseFee(City.PARNU, VehicleType.BIKE, "2.00");
    }

    private void seedExtraFeeRules() {
        for (VehicleType vehicle : List.of(VehicleType.SCOOTER, VehicleType.BIKE)) {
            numericRule(RuleCategory.AIR_TEMPERATURE, vehicle, null, false, -10.0, false, "1.00");
            numericRule(RuleCategory.AIR_TEMPERATURE, vehicle, -10.0, true, 0.0, true, "0.50");
            textRule(vehicle, "snow", "1.00");
            textRule(vehicle, "sleet", "1.00");
            textRule(vehicle, "rain", "0.50");
            textRule(vehicle, "shower", "0.50");
            textRule(vehicle, "glaze", null);
            textRule(vehicle, "hail", null);
            textRule(vehicle, "thunder", null);
        }
        numericRule(RuleCategory.WIND_SPEED, VehicleType.BIKE, 10.0, true, 20.0, true, "0.50");
        numericRule(RuleCategory.WIND_SPEED, VehicleType.BIKE, 20.0, false, null, false, null);
    }

    private void baseFee(City city, VehicleType vehicle, String fee) {
        BaseFee baseFee = new BaseFee();
        baseFee.setCity(city);
        baseFee.setVehicleType(vehicle);
        baseFee.setBaseFee(new BigDecimal(fee));
        baseFee.setValidFrom(Instant.EPOCH);
        baseFeeRepository.save(baseFee);
    }

    private void numericRule(RuleCategory category, VehicleType vehicle, Double min, boolean minInclusive, Double max, boolean maxInclusive, String fee) {
        ExtraFeeRule rule = newRule(category, vehicle, fee);
        rule.setMinValue(min);
        rule.setMinInclusive(minInclusive);
        rule.setMaxValue(max);
        rule.setMaxInclusive(maxInclusive);
        ruleRepository.save(rule);
    }

    private void textRule(VehicleType vehicle, String matchText, String fee) {
        ExtraFeeRule rule = newRule(RuleCategory.PHENOMENON, vehicle, fee);
        rule.setMatchText(matchText);
        ruleRepository.save(rule);
    }

    private ExtraFeeRule newRule(RuleCategory category, VehicleType vehicle, String fee) {
        ExtraFeeRule rule = new ExtraFeeRule();
        rule.setCategory(category);
        rule.setVehicleType(vehicle);
        rule.setForbidden(fee == null);
        rule.setFee(fee == null ? null : new BigDecimal(fee));
        rule.setValidFrom(Instant.EPOCH);
        return rule;
    }
}
