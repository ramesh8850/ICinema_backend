package com.infy.icinema.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Long id;

    private String transactionId;

    @jakarta.validation.constraints.NotNull(message = "Amount paid cannot be null")
    private Double amountPaid;

    private LocalDateTime paymentTime;

    @jakarta.validation.constraints.NotNull(message = "Payment mode cannot be null")
    private String paymentMode; // 'CREDIT_CARD', 'DEBIT_CARD'

    @jakarta.validation.constraints.NotNull(message = "Booking ID cannot be null")
    private Long bookingId;
}
