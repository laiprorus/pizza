package com.example.pizza;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InternalServerException extends ResponseStatusException {

    public InternalServerException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
