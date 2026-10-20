package com.liisabot.delivery.fees.exception;

import org.springframework.http.HttpStatus;

public class WeatherDataNotFoundException extends ApiException {
    public WeatherDataNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
