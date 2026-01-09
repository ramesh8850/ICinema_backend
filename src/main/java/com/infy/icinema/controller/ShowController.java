package com.infy.icinema.controller;

import com.infy.icinema.dto.ShowDTO;
import com.infy.icinema.service.ShowService;
import com.infy.icinema.utility.ResponseHandler;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shows")
public class ShowController {

    @Autowired
    private ShowService showService;

    @GetMapping("/{movieId}")
    public ResponseEntity<Object> getShowsByMovie(@PathVariable Long movieId) {
        return new ResponseEntity<>(ResponseHandler.generateResponse("Shows retrieved successfully", HttpStatus.OK,
                showService.getShowsByMovie(movieId)), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> addShow(@Valid @RequestBody ShowDTO showDTO) {
        return new ResponseEntity<>(ResponseHandler.generateResponse("Show added successfully", HttpStatus.CREATED,
                showService.addShow(showDTO)), HttpStatus.CREATED);
    }

    @GetMapping("/{showId}/seats")
    public ResponseEntity<Object> getShowSeats(@PathVariable Long showId) {
        return new ResponseEntity<>(ResponseHandler.generateResponse("Show seats retrieved successfully", HttpStatus.OK,
                showService.getShowSeats(showId)), HttpStatus.OK);
    }

    @GetMapping("/id/{showId}")
    public ResponseEntity<Object> getShowById(@PathVariable Long showId) {
        return new ResponseEntity<>(ResponseHandler.generateResponse("Show retrieved successfully", HttpStatus.OK,
                showService.getShowById(showId)), HttpStatus.OK);
    }
}
