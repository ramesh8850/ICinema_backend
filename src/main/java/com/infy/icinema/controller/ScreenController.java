package com.infy.icinema.controller;

import com.infy.icinema.dto.ScreenDTO;
import com.infy.icinema.service.ScreenService;
import com.infy.icinema.utility.ResponseHandler;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/screens")

public class ScreenController {

    @Autowired
    private ScreenService screenService;

    @PostMapping
    public ResponseEntity<Object> addScreen(@Valid @RequestBody ScreenDTO screenDTO) {
        return new ResponseEntity<>(ResponseHandler.generateResponse("Screen added successfully", HttpStatus.CREATED,
                screenService.addScreen(screenDTO)), HttpStatus.CREATED);
    }
}
