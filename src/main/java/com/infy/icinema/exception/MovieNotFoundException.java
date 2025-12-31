package com.infy.icinema.exception;

public class MovieNotFoundException extends ResourceNotFoundException {
    public MovieNotFoundException(String message) {
        super(message);
    }
}
