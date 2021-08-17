package com.example.pizza.orders.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.pizza.config.OrderConstraints;

public class InvalidSizeException extends ResponseStatusException {

    public InvalidSizeException(String size) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid size value \'" + size + "\'. Allowed values: " + String.join(", ", OrderConstraints.size) + ".");
    }
}