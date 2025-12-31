package com.infy.icinema.dto;

import lombok.Data;

@Data
public class ShowSeatDTO {
    private Long id; // showSeatId
    private String status; // 'AVAILABLE', 'BOOKED', 'BLOCKED'
    private Double price;
    private Long showId;

    // Details from the physical Seat
    private Long seatId;
    private String rowName;
    private Integer seatNumber;
    private String seatType;
}
