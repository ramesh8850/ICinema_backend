package com.infy.icinema.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatUpdateDTO {
    private Long showId;
    private Long seatId;
    private String status; // 'BOOKED', 'BLOCKED'
    private String message;
    private Long bookedByUserId;
}
