package com.infy.icinema.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class BookingDTO {
    private Long id;
    private LocalDateTime bookingDate;

    private Double totalAmount;
    private Double seatCost;
    private Double convenienceFee;
    private Double gst;

    private String transactionId;
    private Double discountAmount;

    private String bookingStatus;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Show ID cannot be null")
    private Long showId;
    private String movieTitle;
    private String theatreName;
    private String city;
    private List<String> seatNumbers;
    private LocalDate showDate;
    private LocalTime showTime;

    @NotEmpty(message = "At least one seat must be selected")
    private List<Long> showSeatIds;
}
