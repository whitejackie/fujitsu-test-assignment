package com.liisabot.delivery.fees.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.liisabot.delivery.fees.domain.ExtraFeeRule;
import com.liisabot.delivery.fees.domain.RuleCategory;
import com.liisabot.delivery.fees.exception.InvalidInputException;
import com.liisabot.delivery.fees.exception.ResourceNotFoundException;
import com.liisabot.delivery.fees.repository.ExtraFeeRuleRepository;

/** Create, read, update and delete weather extra fee rules. */
@Service 
public class ExtraFeeRuleService {

    private final ExtraFeeRuleRepository repository;

    public ExtraFeeRuleService(ExtraFeeRuleRepository repository) {
        this.repository = repository;
    }

    /**
     * Returns all extra fee rules.
     */
    public List<ExtraFeeRule> findAll() {
        return repository.findAll();
    }

    /**
     * Returns one extra fee rule by id.
     * 
     * @throws ResourceNotFoundException if there is no extra fee with this id
     */
    public ExtraFeeRule findById(Long id) {
        Optional<ExtraFeeRule> result = repository.findById(id);
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("Extra fee " + id + " not found");
        }
        return result.get();
    }

    /**
     * Saves a new extra fee rule.
     * 
     * @throws InvalidInputException if a field is missing or wrong
     */
    public ExtraFeeRule create(ExtraFeeRule rule) {
        rule.setId(null);
        check(rule);
        return repository.save(rule);
    }

    /**
     * Replaces an existing rule.
     * 
     * @throws ResourceNotFoundException if there is no rule with this id
     * @throws InvalidInputException if a field is missing or wrong
     */
    public ExtraFeeRule update(Long id, ExtraFeeRule rule) {
        findById(id);
        rule.setId(id);
        check(rule);
        return repository.save(rule);
    }

    /**
     * Deletes a rule.
     * 
     * @throws ResourceNotFoundException if there is no rule with this id
     */
    public void delete(Long id) {
        ExtraFeeRule existing = findById(id);
        repository.delete(existing);
    }

    private void check(ExtraFeeRule rule) {
        boolean isNumberRule = rule.getCategory() == RuleCategory.AIR_TEMPERATURE || rule.getCategory() == RuleCategory.WIND_SPEED;
        if (rule.getCategory() == null) {
            throw new InvalidInputException("category is required");
        }
        if (rule.getVehicleType() == null) {
            throw new InvalidInputException("vehicleType is required");
        }
        if (rule.getValidFrom() == null) {
            throw new InvalidInputException("validFrom is required");
        }
        if (rule.getValidTo() != null && !rule.getValidTo().isAfter(rule.getValidFrom())) {
            throw new InvalidInputException("validTo must be after validFrom");
        }
        if (!rule.isForbidden() && rule.getFee() == null) {
            throw new InvalidInputException("fee is required unless the rule is forbidden");
        }
        if (rule.getCategory() == RuleCategory.PHENOMENON
        && (rule.getMatchText() == null || rule.getMatchText().isBlank())) {
            throw new InvalidInputException("matchText is required for PHENOMENON rules");
        }
        if (isNumberRule && rule.getMinValue() == null && rule.getMaxValue() == null) {
            throw new InvalidInputException("minValue or maxValue is required for temperature and wind rules");
        }
        if (isNumberRule && rule.getMinValue() != null && rule.getMaxValue() != null 
        && rule.getMinValue() > rule.getMaxValue()) {
            throw new InvalidInputException("minValue cannot be greater than maxValue");
        }
        if (rule.isForbidden()) {
            rule.setFee(null);
        }
    }
}
