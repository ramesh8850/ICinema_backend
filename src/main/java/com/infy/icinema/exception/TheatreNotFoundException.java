package com.infy.icinema.exception;

public class TheatreNotFoundException extends ResourceNotFoundException {
    public TheatreNotFoundException(String message) {
        super(message);
    }
}
