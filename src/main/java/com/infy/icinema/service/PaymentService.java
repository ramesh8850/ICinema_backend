package com.infy.icinema.service;

import com.infy.icinema.dto.PaymentDTO;

public interface PaymentService {
    PaymentDTO makePayment(PaymentDTO paymentDTO);
}
