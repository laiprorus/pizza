package com.example.pizza.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNameEmptyException extends ResponseStatusException {

    public UserNameEmptyException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "User name cannot be null or empty.");
    }
}