package com.example.pizza.orders.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.pizza.config.OrderConstraints;

public class InvalidCrustException extends ResponseStatusException {

    public InvalidCrustException(String crust) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid crust value \'" + crust + "\'. Allowed values: " + String.join(", ", OrderConstraints.crust) + ".");
    }
}