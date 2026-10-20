package com.liisabot.delivery.fees.exception;

import org.springframework.http.HttpStatus;

/** The weather makes the selected vehicle type unusable (HTTP 422) */
public class VehicleForbiddenException extends ApiException {
    public VehicleForbiddenException() {
        super(HttpStatus.UNPROCESSABLE_CONTENT, "Usage of selected vehicle type is forbidden");
    }
}
