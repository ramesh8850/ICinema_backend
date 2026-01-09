package com.infy.icinema.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Long id;

    private String transactionId;

    @NotNull(message = "Amount paid cannot be null")
    private Double amountPaid;

    private LocalDateTime paymentTime;

    @NotNull(message = "Payment mode cannot be null")
    private String paymentMode; // 'CREDIT_CARD', 'DEBIT_CARD'

    @NotNull(message = "Booking ID cannot be null")
    private Long bookingId;
}
