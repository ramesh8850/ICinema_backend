package com.infy.icinema.controller;

import com.infy.icinema.dto.UserDTO;
import com.infy.icinema.service.UserService;
import com.infy.icinema.utility.ResponseHandler;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")

public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody UserDTO userDTO) {
        return new ResponseEntity<>(ResponseHandler.generateResponse("User registered successfully!",
                HttpStatus.CREATED, userService.registerUser(userDTO)), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody UserDTO loginDetails) {
        // Validation could be added here too if LoginDTO existed, or partial validation
        return new ResponseEntity<>(ResponseHandler.generateResponse("Login successful!", HttpStatus.OK,
                userService.loginUser(loginDetails.getEmail(), loginDetails.getPassword())), HttpStatus.OK);
    }
}
