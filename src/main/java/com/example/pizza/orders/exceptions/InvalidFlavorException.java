package com.example.pizza.orders.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.pizza.config.OrderConstraints;

public class InvalidFlavorException extends ResponseStatusException {

    public InvalidFlavorException(String flavor) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid flavor value \'" + flavor + "\'. Allowed values: " + String.join(", ", OrderConstraints.flavor) + ".");
    }
}