package com.infy.icinema.utility;

import com.infy.icinema.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> handleGeneralException(Exception exception) {
        ErrorInfo error = new ErrorInfo();
        error.setErrorMessage(exception.getMessage());
        error.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setErrorTimeStamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleResourceNotFoundException(ResourceNotFoundException exception) {
        ErrorInfo error = new ErrorInfo();
        error.setErrorMessage(exception.getMessage());
        error.setErrorCode(HttpStatus.NOT_FOUND.value());
        error.setErrorTimeStamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorInfo> handleValidationException(
            org.springframework.web.bind.MethodArgumentNotValidException exception) {
        String errorMessage = exception.getBindingResult().getAllErrors().stream()
                .map(org.springframework.validation.ObjectError::getDefaultMessage)
                .collect(java.util.stream.Collectors.joining(", "));

        ErrorInfo error = new ErrorInfo();
        error.setErrorMessage(errorMessage);
        error.setErrorCode(HttpStatus.BAD_REQUEST.value());
        error.setErrorTimeStamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<ErrorInfo> handleConstraintViolationException(
            jakarta.validation.ConstraintViolationException exception) {
        String errorMessage = exception.getConstraintViolations().stream()
                .map(jakarta.validation.ConstraintViolation::getMessage)
                .collect(java.util.stream.Collectors.joining(", "));

        ErrorInfo error = new ErrorInfo();
        error.setErrorMessage(errorMessage);
        error.setErrorCode(HttpStatus.BAD_REQUEST.value());
        error.setErrorTimeStamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Explicit handlers for custom exceptions as requested
    @ExceptionHandler({
            com.infy.icinema.exception.UserNotFoundException.class,
            com.infy.icinema.exception.MovieNotFoundException.class,
            com.infy.icinema.exception.TheatreNotFoundException.class,
            com.infy.icinema.exception.ScreenNotFoundException.class,
            com.infy.icinema.exception.ShowNotFoundException.class,
            com.infy.icinema.exception.BookingNotFoundException.class
    })
    public ResponseEntity<ErrorInfo> handleCustomNotFoundExceptions(ResourceNotFoundException exception) {
        ErrorInfo error = new ErrorInfo();
        error.setErrorMessage(exception.getMessage());
        error.setErrorCode(HttpStatus.NOT_FOUND.value());
        error.setErrorTimeStamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Can add more specific handlers if needed, but ResourceNotFoundException
    // covers the custom ones
}
