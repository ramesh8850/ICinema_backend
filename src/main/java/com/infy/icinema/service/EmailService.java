package com.infy.icinema.service;

import com.infy.icinema.dto.BookingDTO;

public interface EmailService {
    void sendBookingConfirmation(String toEmail, BookingDTO bookingDTO);
}
