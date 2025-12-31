package com.infy.icinema.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long id;

    @jakarta.validation.constraints.NotNull(message = "Rating cannot be null")
    @jakarta.validation.constraints.Min(value = 1, message = "Rating must be at least 1")
    @jakarta.validation.constraints.Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    private String comment;

    @jakarta.validation.constraints.NotNull(message = "Movie ID cannot be null")
    private Long movieId;

    @jakarta.validation.constraints.NotNull(message = "User ID cannot be null")
    private Long userId;

    private String username; // To display who reviewed
}
