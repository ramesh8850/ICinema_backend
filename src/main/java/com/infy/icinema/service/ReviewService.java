package com.infy.icinema.service;

import com.infy.icinema.dto.ReviewDTO;

public interface ReviewService {
    ReviewDTO addReview(ReviewDTO reviewDTO);

    java.util.List<ReviewDTO> getReviewsByMovie(Long movieId);
}
