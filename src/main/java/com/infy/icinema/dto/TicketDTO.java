package com.infy.icinema.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class TicketDTO {
    private String movieTitle;
    private String theatreName;
    private String screenName; // Added for ticket display
    private String city;
    private List<String> seatNumbers; // Row + Number (e.g., "A1", "B2")
    private String seatType; // Added for ticket display (e.g. "Platinum, Gold")
    private String censorRating;

    // Price Summary
    private Double seatCost;
    private Double convenienceFee;
    private Double gst;
    private Double discountAmount; // Added for displaying discount
    private Double totalAmount;

    private String transactionId;

    private LocalDate showDate;

    private LocalTime showTime;
    private String moviePosterUrl;
    private String qrPayload; // For secure QR code display in frontend
}
