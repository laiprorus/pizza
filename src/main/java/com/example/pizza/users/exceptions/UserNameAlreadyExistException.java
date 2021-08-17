package com.example.pizza.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNameAlreadyExistException extends ResponseStatusException {

    public UserNameAlreadyExistException(String name) {
        super(HttpStatus.CONFLICT, "User with name \'" + name + "\' already exists.");
    }
}