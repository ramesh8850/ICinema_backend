package com.infy.icinema.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

import java.io.Serializable;

@Data
public class MovieDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Genre cannot be blank")
    private String genre;

    @NotBlank(message = "Language cannot be blank")
    private String language;

    private String description;
    private String imageUrl;

    @NotNull(message = "Release date cannot be null")
    private LocalDate releaseDate;

    @NotNull(message = "Duration cannot be null")
    @Min(value = 1, message = "Duration must be greater than 0")
    private Integer durationMinutes;

    private String censorRating;
    private Double averageRating;
}
