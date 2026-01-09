package com.infy.icinema.controller;

import com.infy.icinema.dto.MovieDTO;
import com.infy.icinema.service.MovieService;
import com.infy.icinema.utility.ResponseHandler;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

        @Autowired
        private MovieService movieService;

        @GetMapping
        public ResponseEntity<Object> getAllMovies() {
                return new ResponseEntity<>(
                                ResponseHandler.generateResponse("Movies retrieved successfully", HttpStatus.OK,
                                                movieService.getAllMovies()),
                                HttpStatus.OK);
        }

        @GetMapping("/{id}")
        public ResponseEntity<Object> getMovieById(@PathVariable Long id) {
                return new ResponseEntity<>(
                                ResponseHandler.generateResponse("Movie retrieved successfully", HttpStatus.OK,
                                                movieService.getMovieById(id)),
                                HttpStatus.OK);
        }

        @PostMapping
        public ResponseEntity<Object> addMovie(@Valid @RequestBody MovieDTO movieDTO) {
                return new ResponseEntity<>(
                                ResponseHandler.generateResponse("Movie added successfully", HttpStatus.CREATED,
                                                movieService.addMovie(movieDTO)),
                                HttpStatus.CREATED);
        }

        @GetMapping("/search")
        public ResponseEntity<Object> searchMovies(@RequestParam String query) {
                return new ResponseEntity<>(
                                ResponseHandler.generateResponse("Movies retrieved successfully", HttpStatus.OK,
                                                movieService.searchMovies(query)),
                                HttpStatus.OK);
        }

        @GetMapping("/filter")
        public ResponseEntity<Object> filterMovies(@RequestParam(required = false) String title,
                        @RequestParam(required = false) String genre,
                        @RequestParam(required = false) String language,
                        @RequestParam(required = false) Double rating) {
                return new ResponseEntity<>(
                                ResponseHandler.generateResponse("Movies filtered successfully", HttpStatus.OK,
                                                movieService.filterMovies(title, genre, language, rating)),
                                HttpStatus.OK);
        }
}
