package com.infy.icinema.controller;

import com.infy.icinema.dto.PaymentDTO;
import com.infy.icinema.service.PaymentService;
import com.infy.icinema.utility.ResponseHandler;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Object> makePayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        return new ResponseEntity<>(ResponseHandler.generateResponse("Payment successful", HttpStatus.CREATED,
                paymentService.makePayment(paymentDTO)), HttpStatus.CREATED);
    }
}
