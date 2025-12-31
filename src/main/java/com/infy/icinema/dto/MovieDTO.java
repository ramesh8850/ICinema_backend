package com.infy.icinema.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MovieDTO {
    private Long id;

    @jakarta.validation.constraints.NotBlank(message = "Title cannot be blank")
    private String title;

    @jakarta.validation.constraints.NotBlank(message = "Genre cannot be blank")
    private String genre;

    @jakarta.validation.constraints.NotBlank(message = "Language cannot be blank")
    private String language;

    private String description;
    private String imageUrl;

    @jakarta.validation.constraints.NotNull(message = "Release date cannot be null")
    private LocalDate releaseDate;

    @jakarta.validation.constraints.NotNull(message = "Duration cannot be null")
    @jakarta.validation.constraints.Min(value = 1, message = "Duration must be greater than 0")
    private Integer durationMinutes;

    private String censorRating;
    private Double averageRating;
}
