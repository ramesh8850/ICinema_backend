package com.infy.icinema.service.impl;

import com.infy.icinema.dto.MovieDTO;
import com.infy.icinema.entity.Movie;
import com.infy.icinema.exception.MovieNotFoundException;
import com.infy.icinema.repository.MovieRepository;
import com.infy.icinema.service.MovieService;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MovieServiceImpl implements MovieService {
        @Autowired
        private MovieRepository movieRepository;

        @Autowired
        private ModelMapper modelMapper;

        @Override
        @Cacheable(value = "movies")
        public List<MovieDTO> getAllMovies() {
                return movieRepository.findAll().stream()
                                .map(movie -> modelMapper.map(movie, MovieDTO.class))
                                .collect(Collectors.toList());
        }

        @Override
        @Cacheable(value = "movie", key = "#id")
        public MovieDTO getMovieById(Long id) {
                Movie movie = movieRepository.findById(id)
                                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + id));
                return modelMapper.map(movie, MovieDTO.class);
        }

        @Override
        @CacheEvict(value = "movies", allEntries = true)
        public MovieDTO addMovie(MovieDTO movieDTO) {
                Movie movie = modelMapper.map(movieDTO, Movie.class);
                Movie savedMovie = movieRepository.save(movie);
                return modelMapper.map(savedMovie, MovieDTO.class);
        }

        @Override
        public List<MovieDTO> searchMovies(String keyword) {
                return movieRepository
                                .findByTitleContainingIgnoreCaseOrGenreContainingIgnoreCaseOrLanguageContainingIgnoreCase(
                                                keyword,
                                                keyword, keyword)
                                .stream()
                                .map(movie -> modelMapper.map(movie, MovieDTO.class))
                                .collect(Collectors.toList());
        }

        @Override
        public List<MovieDTO> filterMovies(String title, String genre, String language, Double rating) {
                return movieRepository.filterMovies(title, genre, language, rating).stream()
                                .map(movie -> modelMapper.map(movie, MovieDTO.class))
                                .collect(Collectors.toList());
        }
}
