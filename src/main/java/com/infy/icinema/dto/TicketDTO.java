package com.infy.icinema.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class TicketDTO {
    private String movieTitle;
    private String theatreName;
    private String city;
    private List<String> seatNumbers; // Row + Number (e.g., "A1", "B2")
    private String censorRating;

    // Price Summary
    private Double seatCost;
    private Double convenienceFee;
    private Double gst;
    private Double totalAmount;

    private String transactionId;

    private LocalDate showDate;
    private LocalTime showTime;
}
