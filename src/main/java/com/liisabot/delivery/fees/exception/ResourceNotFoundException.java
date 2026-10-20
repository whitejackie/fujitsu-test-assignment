package com.liisabot.delivery.fees.exception;

import org.springframework.http.HttpStatus;

/** A request record does not exist (HTTP 404) */
public class ResourceNotFoundException extends ApiException{
    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
