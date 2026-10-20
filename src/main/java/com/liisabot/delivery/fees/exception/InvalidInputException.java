package com.liisabot.delivery.fees.exception;

import org.springframework.http.HttpStatus;

/** The client sent an invalid parameter (HTTP 400) */
public class InvalidInputException extends ApiException {
    public InvalidInputException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
