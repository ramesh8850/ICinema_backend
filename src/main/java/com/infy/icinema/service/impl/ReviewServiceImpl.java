package com.infy.icinema.service.impl;

import com.infy.icinema.dto.ReviewDTO;
import com.infy.icinema.entity.Movie;
import com.infy.icinema.entity.Review;
import com.infy.icinema.entity.User;
import com.infy.icinema.exception.MovieNotFoundException;
import com.infy.icinema.exception.UserNotFoundException;
import com.infy.icinema.repository.MovieRepository;
import com.infy.icinema.repository.ReviewRepository;
import com.infy.icinema.repository.UserRepository;
import com.infy.icinema.service.ReviewService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ReviewDTO addReview(ReviewDTO reviewDTO) {
        User user = userRepository.findById(reviewDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + reviewDTO.getUserId()));
        Movie movie = movieRepository.findById(reviewDTO.getMovieId())
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + reviewDTO.getMovieId()));

        if (reviewRepository.existsByUser_IdAndMovie_Id(user.getId(), movie.getId())) {
            throw new RuntimeException("User has already reviewed this movie!");
        }

        Review review = modelMapper.map(reviewDTO, Review.class);
        review.setUser(user);
        review.setMovie(movie);

        Review savedReview = reviewRepository.save(review);
        reviewRepository.flush(); // Ensure it's in DB before calc

        // Recalculate Average Rating
        List<Review> movieReviews = reviewRepository.findByMovie_Id(movie.getId());

        double average = movieReviews.stream().mapToInt(Review::getRating).average().orElse(0.0);

        // Round to 1 decimal place
        average = Math.round(average * 10.0) / 10.0;

        movie.setAverageRating(average);
        movieRepository.save(movie);

        ReviewDTO savedDTO = modelMapper.map(savedReview, ReviewDTO.class);
        savedDTO.setUsername(user.getUsername());
        return savedDTO;
    }

    @Override
    public List<ReviewDTO> getReviewsByMovie(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new MovieNotFoundException("Movie not found with id: " + movieId);
        }
        return reviewRepository.findByMovie_Id(movieId).stream()
                .map(review -> {
                    ReviewDTO dto = modelMapper.map(review, ReviewDTO.class);
                    dto.setUsername(review.getUser().getUsername());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
