package com.liisabot.delivery.fees.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.liisabot.delivery.fees.domain.City;
import com.liisabot.delivery.fees.domain.VehicleType;
import com.liisabot.delivery.fees.dto.DeliveryFeeResponse;
import com.liisabot.delivery.fees.service.DeliveryFeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.Instant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** REST endpoint for asking what delivery costs. */
@RestController 
@RequestMapping("/api/delivery-fee")
@Tag (name = "Delivery fee")
public class DeliveryFeeController {

    private final DeliveryFeeService service;

    public DeliveryFeeController(DeliveryFeeService service) {
        this.service = service;
    }


    /**
     * Returns the delivery fee for a city and vehicle type.
     * 
     * @param city          Tallinn, Tartu or Pärnu
     * @param vehicleType   Car, Scooter and Bike
     * @param datetime      optional moment in ISO format, e.g. 2026-10-19T10:00:00Z
     * @return the total fee
     */
    @GetMapping
    @Operation (summary = "Calculate the delivery fee", 
        description = "Uses the latest weather, or the weather and rules valid at 'datetime' if it is given."
    )
    public DeliveryFeeResponse getFee(@RequestParam String city, 
        @RequestParam String vehicleType,
        @RequestParam(required = false) Instant datetime
    ) {
        City parsedCity = City.fromInput(city);
        VehicleType parsedVehicle = VehicleType.fromInput(vehicleType);
        return service.calculate(parsedCity, parsedVehicle, datetime);
    }
    
}
