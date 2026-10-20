package com.liisabot.delivery.fees.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.liisabot.delivery.fees.domain.BaseFee;
import com.liisabot.delivery.fees.exception.InvalidInputException;
import com.liisabot.delivery.fees.exception.ResourceNotFoundException;
import com.liisabot.delivery.fees.repository.BaseFeeRepository;

@Service 
public class BaseFeeService {

    private final BaseFeeRepository repository;

    public BaseFeeService(BaseFeeRepository repository) {
        this.repository = repository;
    }

    public List<BaseFee> findAll() {
        return repository.findAll();
    }

    public BaseFee findById(Long id) {
        Optional<BaseFee> result = repository.findById(id);
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("Base fee " + id + " not found");
        }
        return result.get();
    }

    public BaseFee create(BaseFee baseFee) {
        baseFee.setId(null);
        check(baseFee);
        return repository.save(baseFee);
    }

    public BaseFee update(Long id, BaseFee baseFee) {
        findById(id);
        baseFee.setId(id);
        check(baseFee);
        return repository.save(baseFee);
    }

    public void delete(Long id) {
        BaseFee existing = findById(id);
        repository.delete(existing);
    }

    private void check(BaseFee baseFee) {
        if (baseFee.getCity() == null) {
            throw new InvalidInputException("city is required");
        }
        if (baseFee.getVehicleType() == null) {
            throw new InvalidInputException("vehicleType is required");
        }
        if (baseFee.getBaseFee() == null || baseFee.getBaseFee().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidInputException("baseFee is required and cannot be negative");
        }
        if (baseFee.getValidFrom() == null) {
            throw new InvalidInputException("validFrom is required");
        }
        if (baseFee.getValidTo() != null && !baseFee.getValidTo().isAfter(baseFee.getValidFrom())) {
            throw new InvalidInputException("validTo must be after validFrom");
        }
    }
}
