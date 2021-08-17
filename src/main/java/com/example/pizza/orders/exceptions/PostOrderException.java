package com.example.pizza.orders.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PostOrderException extends ResponseStatusException {

    public PostOrderException() {
        super(HttpStatus.FAILED_DEPENDENCY, "Error saving order.");
    }
}