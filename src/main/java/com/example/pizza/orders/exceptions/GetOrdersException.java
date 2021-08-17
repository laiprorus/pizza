package com.example.pizza.orders.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class GetOrdersException extends ResponseStatusException {

    public GetOrdersException() {
        super(HttpStatus.FAILED_DEPENDENCY, "Error retrieving orders.");
    }
}