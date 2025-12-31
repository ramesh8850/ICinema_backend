package com.infy.icinema.exception;

public class ScreenNotFoundException extends ResourceNotFoundException {
    public ScreenNotFoundException(String message) {
        super(message);
    }
}
