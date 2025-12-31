package com.infy.icinema.dto;

import lombok.Data;

@Data
public class ScreenDTO {
    private Long id;

    @jakarta.validation.constraints.NotBlank(message = "Screen name cannot be blank")
    private String screenName;

    @jakarta.validation.constraints.Min(value = 1, message = "Total seats must be at least 1")
    private Integer totalSeats; // Integer matches wrapper type in previous view

    @jakarta.validation.constraints.NotNull(message = "Theatre ID cannot be null")
    private Long theatreId;
}
