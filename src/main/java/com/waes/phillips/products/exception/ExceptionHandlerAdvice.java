package com.waes.phillips.products.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(value = ProductException.class)
    public ResponseEntity<Map<String, String>> serviceErrorHandler(Exception exception) {
        return new ResponseEntity<>(createBody(exception, HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Map<String, String>> defaultErrorHandler(Exception exception) {
        return new ResponseEntity<>(createBody(exception, HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, String> createBody(Exception exception, int status) {
        Map<String, String> body = new HashMap<>();
        body.put("status", String.valueOf(status));
        body.put("message", exception.getMessage());
        body.put("cause", getRootCause(exception));
        return body;
    }

    private String getRootCause(Exception exception) {
        return exception.getCause() != null ? sanitizeMessage(exception.getCause().getMessage()) :
                exception.getClass().getSimpleName();
    }

    private String sanitizeMessage(String causeMessage) {
        if (causeMessage.indexOf("trace") > 1) {
            causeMessage = causeMessage.substring(0, causeMessage.indexOf("trace"));
        }
        return causeMessage;
    }

}