package com.infy.icinema.controller;

import com.infy.icinema.dto.ReviewDTO;
import com.infy.icinema.service.ReviewService;
import com.infy.icinema.utility.ResponseHandler;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Object> addReview(@Valid @RequestBody ReviewDTO reviewDTO) {
        System.out.println(
                "DEBUG: addReview called. UserID: " + reviewDTO.getUserId() + ", MovieID: " + reviewDTO.getMovieId());
        return new ResponseEntity<>(ResponseHandler.generateResponse("Review added successfully", HttpStatus.CREATED,
                reviewService.addReview(reviewDTO)), HttpStatus.CREATED);
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<Object> getReviewsByMovie(@PathVariable Long movieId) {
        return new ResponseEntity<>(ResponseHandler.generateResponse("Reviews retrieved successfully", HttpStatus.OK,
                reviewService.getReviewsByMovie(movieId)), HttpStatus.OK);
    }
}
