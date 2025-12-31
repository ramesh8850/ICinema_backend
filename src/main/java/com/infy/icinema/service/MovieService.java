package com.infy.icinema.service;

import com.infy.icinema.dto.MovieDTO;

public interface MovieService {
        java.util.List<MovieDTO> getAllMovies();

        MovieDTO getMovieById(Long id);

        MovieDTO addMovie(MovieDTO movieDTO);

        java.util.List<MovieDTO> searchMovies(String keyword);

        java.util.List<MovieDTO> filterMovies(String title, String genre, String language, Double rating);
}
