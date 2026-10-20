package com.liisabot.delivery.fees.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.liisabot.delivery.fees.domain.BaseFee;
import com.liisabot.delivery.fees.service.BaseFeeService;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping("/api/base-fees")
@Tag(name = "Base fees")
public class BaseFeeController {

    private final BaseFeeService service;

    public BaseFeeController(BaseFeeService service) {
        this.service = service;
    }

    /** Lists all base fees. */
    @GetMapping
    public List<BaseFee> list() {
        return service.findAll();
    }
    
    /** Returns one base fee by id. */
    @GetMapping("/{id}")
    public BaseFee get(@PathVariable Long id) {
        return service.findById(id);
    }

    /** Creates a new base fee. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BaseFee create(@RequestBody BaseFee baseFee) {
        return service.create(baseFee);
    }

    /** Replaces an existing base fee. */
    @PutMapping("/{id}")
    public BaseFee update(@PathVariable Long id, @RequestBody BaseFee baseFee) {
        return service.update(id, baseFee);    
    }

    /** Deletes a base fee. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
