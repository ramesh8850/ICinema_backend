package com.infy.icinema.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ShowDTO {
    private Long id;

    @jakarta.validation.constraints.NotNull(message = "Show date cannot be null")
    @jakarta.validation.constraints.FutureOrPresent(message = "Show date must be in the present or future")
    private LocalDate showDate;

    @jakarta.validation.constraints.NotNull(message = "Show time cannot be null")
    private LocalTime showTime;

    // Prices are calculated, but if passed they should be positive.
    // Assuming backend calculates or validates these, but DTO might carry them for
    // display or update.
    // For addShow, they are generated. If manual overrides allowed:
    @jakarta.validation.constraints.Min(value = 0, message = "Price cannot be negative")
    private Double priceSilver;

    @jakarta.validation.constraints.Min(value = 0, message = "Price cannot be negative")
    private Double priceGold;

    @jakarta.validation.constraints.Min(value = 0, message = "Price cannot be negative")
    private Double pricePlatinum;

    @jakarta.validation.constraints.NotNull(message = "Movie ID cannot be null")
    private Long movieId;

    @jakarta.validation.constraints.NotNull(message = "Screen ID cannot be null")
    private Long screenId;
    private String movieTitle; // For display convenience
    private String screenName; // For display convenience
    private String theatreName; // For display convenience
    private String theatreCity;
}
