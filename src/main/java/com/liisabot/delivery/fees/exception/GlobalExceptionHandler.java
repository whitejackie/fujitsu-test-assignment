package com.liisabot.delivery.fees.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.liisabot.delivery.fees.dto.ErrorResponse;

@RestControllerAdvice 
public class GlobalExceptionHandler {

    /** Covered API exceptions(unknown city, weather forbids the vehicle, nothing found...). */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        ErrorResponse body = new ErrorResponse(ex.getStatus().value(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    /** A required request parameter is missing, e.g. no city in the URL. */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex) {
        ErrorResponse body = new ErrorResponse(400, ex.getParameterName() + " is required");
        return ResponseEntity.badRequest().body(body);
    }

    /** The request has a wrong format, e.g. datetime=abc or broken JSON. */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleBadFormat(Exception ex) {
        ErrorResponse body = new ErrorResponse(400, "Invalid request parameter or body format");
        return ResponseEntity.badRequest().body(body);
    }

    /** Anything unexpected. The details are not shown to the client. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        ErrorResponse body = new ErrorResponse(500, "Something went wrong");
        return ResponseEntity.status(500).body(body);
    }
}
