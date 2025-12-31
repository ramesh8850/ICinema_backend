package com.infy.icinema.exception;

public class ShowNotFoundException extends ResourceNotFoundException {
    public ShowNotFoundException(String message) {
        super(message);
    }
}
