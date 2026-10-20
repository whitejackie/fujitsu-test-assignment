package com.liisabot.delivery.fees.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record DeliveryFeeResponse(String city, String vehicleType, BigDecimal totalFee, Instant weatherObservedAt) {}
