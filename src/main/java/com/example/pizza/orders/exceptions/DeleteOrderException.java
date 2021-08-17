package com.example.pizza.orders.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DeleteOrderException extends ResponseStatusException {

    public DeleteOrderException(Long id) {
        super(HttpStatus.FAILED_DEPENDENCY, "Error deleting \'" + id + "\' order.");
    }
}