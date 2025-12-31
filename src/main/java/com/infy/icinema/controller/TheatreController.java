package com.infy.icinema.controller;

import com.infy.icinema.dto.TheatreDTO;
import com.infy.icinema.service.TheatreService;
import com.infy.icinema.utility.ResponseHandler;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/theatres")

public class TheatreController {
    @Autowired
    private TheatreService theatreService;

    @GetMapping
    public ResponseEntity<Object> getAllTheatres() {
        return new ResponseEntity<>(ResponseHandler.generateResponse("Theatres retrieved successfully", HttpStatus.OK,
                theatreService.getAllTheatres()), HttpStatus.OK);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<Object> getTheatresByCity(@PathVariable String city) {
        return new ResponseEntity<>(ResponseHandler.generateResponse("Theatres retrieved successfully", HttpStatus.OK,
                theatreService.getTheatresByCity(city)), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> addTheatre(@Valid @RequestBody TheatreDTO theatreDTO) {
        return new ResponseEntity<>(ResponseHandler.generateResponse("Theatre added successfully", HttpStatus.CREATED,
                theatreService.addTheatre(theatreDTO)), HttpStatus.CREATED);
    }
}
