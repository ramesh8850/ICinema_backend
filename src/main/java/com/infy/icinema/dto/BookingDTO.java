package com.infy.icinema.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
public class BookingDTO {
    private Long id;
    private LocalDateTime bookingDate;

    private Double totalAmount;
    private Double seatCost;
    private Double convenienceFee;
    private Double gst;

    private String bookingStatus;

    @jakarta.validation.constraints.NotNull(message = "User ID cannot be null")
    private Long userId;

    @jakarta.validation.constraints.NotNull(message = "Show ID cannot be null")
    private Long showId;
    private String movieTitle;
    private LocalDate showDate;
    private java.time.LocalTime showTime; // explicitly fully qualified to avoid collision if import missing

    @jakarta.validation.constraints.NotEmpty(message = "At least one seat must be selected")
    private java.util.List<Long> showSeatIds;
}
