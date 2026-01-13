package com.infy.icinema.service;

import com.infy.icinema.dto.PaymentDTO;

public interface PaymentService {
    // Payment processing
    PaymentDTO makePayment(PaymentDTO paymentDTO);
}
