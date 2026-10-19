package com.liisabot.delivery.fees.exception;

/**
 * Thrown when weather data cannot be imported
 */
public class WeatherImportException extends RuntimeException {
    public WeatherImportException(String message, Throwable cause) {
        super(message, cause);
    }

}
