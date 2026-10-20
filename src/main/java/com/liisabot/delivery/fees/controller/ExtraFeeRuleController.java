package com.liisabot.delivery.fees.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.liisabot.delivery.fees.domain.ExtraFeeRule;
import com.liisabot.delivery.fees.service.ExtraFeeRuleService;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/** REST endpoints for managing weather extra fee rules. */
@RestController
@RequestMapping("/api/extra-fee-rules")
@Tag(name = "Extra fee rules")
public class ExtraFeeRuleController {

    private final ExtraFeeRuleService service;

    public ExtraFeeRuleController(ExtraFeeRuleService service) {
        this.service = service;
    }

    /** Lists all rules. */
    @GetMapping
    public List<ExtraFeeRule> list() {
        return service.findAll();
    }
    
    /** Returns one extra fee rule by id. */
    @GetMapping("/{id}")
    public ExtraFeeRule get(@PathVariable Long id) {
        return service.findById(id);
    }

    /** Creates a new extra fee rule. */
    @PostMapping 
    @ResponseStatus (HttpStatus.CREATED)
    public ExtraFeeRule create(@RequestBody ExtraFeeRule rule) {
        return service.create(rule);
    }

    /** Replaces an existing extra fee rule. */
    @PutMapping("/{id}")
    public ExtraFeeRule update(@PathVariable Long id, @RequestBody ExtraFeeRule rule) {
        return service.update(id, rule);
    }
    
    /** Deletes a rule. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
