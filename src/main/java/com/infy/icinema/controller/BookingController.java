package com.infy.icinema.controller;

import com.infy.icinema.dto.BookingDTO;
import com.infy.icinema.service.BookingService;
import com.infy.icinema.utility.ResponseHandler;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")

public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<Object> createBooking(@Valid @RequestBody BookingDTO bookingDTO) {
        return new ResponseEntity<>(ResponseHandler.generateResponse("Booking created successfully", HttpStatus.CREATED,
                bookingService.createBooking(bookingDTO)), HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> getBookingsByUser(@PathVariable Long userId) {
        return new ResponseEntity<>(ResponseHandler.generateResponse("Bookings retrieved successfully", HttpStatus.OK,
                bookingService.getBookingsByUser(userId)), HttpStatus.OK);
    }

    @GetMapping("/{bookingId}/ticket")
    public ResponseEntity<Object> getTicketDetails(@PathVariable Long bookingId) {
        return new ResponseEntity<>(
                ResponseHandler.generateResponse("Ticket details retrieved successfully", HttpStatus.OK,
                        bookingService.getTicketDetails(bookingId)),
                HttpStatus.OK);
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<Object> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return new ResponseEntity<>(
                ResponseHandler.generateResponse("Booking cancelled successfully", HttpStatus.OK, null),
                HttpStatus.OK);
    }
}
