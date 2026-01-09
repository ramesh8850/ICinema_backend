package com.infy.icinema.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class ReviewDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

    @NotNull(message = "Rating cannot be null")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    private String comment;

    @NotNull(message = "Movie ID cannot be null")
    private Long movieId;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    private String username; // To display who reviewed
}
