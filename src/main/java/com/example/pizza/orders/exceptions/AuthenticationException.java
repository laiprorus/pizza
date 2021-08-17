package com.example.pizza.orders.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthenticationException extends ResponseStatusException {

    public AuthenticationException() {
        super(HttpStatus.FAILED_DEPENDENCY, "Authentication error.");
    }
}
