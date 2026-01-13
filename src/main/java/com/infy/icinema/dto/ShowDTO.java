package com.infy.icinema.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

import java.io.Serializable;

@Data
public class ShowDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

    @NotNull(message = "Show date cannot be null")
    @FutureOrPresent(message = "Show date must be in the present or future")
    private LocalDate showDate;

    @NotNull(message = "Show time cannot be null")
    private LocalTime showTime;

    // Dynamic Pricing Map: "SILVER" -> 100.0, "GOLD" -> 200.0
    private java.util.Map<String, Double> seatPrices = new java.util.HashMap<>();

    @NotNull(message = "Movie ID cannot be null")
    private Long movieId;

    @NotNull(message = "Screen ID cannot be null")
    private Long screenId;
    private String movieTitle; // For display convenience
    private String screenName; // For display convenience
    private String theatreName; // For display convenience
    private String theatreCity;
}
