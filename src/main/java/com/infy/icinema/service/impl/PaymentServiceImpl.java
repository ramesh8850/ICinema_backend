package com.infy.icinema.service.impl;

import com.infy.icinema.dto.PaymentDTO;
import com.infy.icinema.entity.Booking;
import com.infy.icinema.entity.Payment;
import com.infy.icinema.entity.ShowSeat;
import com.infy.icinema.entity.Ticket;
import com.infy.icinema.repository.BookingRepository;
import com.infy.icinema.repository.PaymentRepository;
import com.infy.icinema.repository.ShowSeatRepository;
import com.infy.icinema.repository.TicketRepository;
import com.infy.icinema.service.PaymentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private ShowSeatRepository showSeatRepository;
    @Autowired
    private com.infy.icinema.service.EmailService emailService;

    @Override
    public PaymentDTO makePayment(PaymentDTO paymentDTO) {
        Booking booking = bookingRepository.findById(paymentDTO.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + paymentDTO.getBookingId()));

        if (!"PENDING".equals(booking.getBookingStatus())) {
            throw new RuntimeException("Booking is already confirmed or invalid status: " + booking.getBookingStatus());
        }

        // Generate unique Transaction ID
        String transactionId = "TXN_"
                + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

        // 1. Confirm Booking
        booking.setBookingStatus("CONFIRMED");
        bookingRepository.save(booking);

        // 2. Confirm Seats (Update ShowSeat status to BOOKED)
        List<Ticket> tickets = ticketRepository.findByBookingId(booking.getId());
        for (Ticket ticket : tickets) {
            ShowSeat seat = ticket.getShowSeat();
            seat.setStatus("BOOKED");
            showSeatRepository.save(seat);
        }

        // 3. Save Payment
        Payment payment = new Payment();

        // --- Enforce Backend Discount Logic ---
        List<Ticket> currentTickets = ticketRepository.findByBookingId(booking.getId());
        double seatCost = round(currentTickets.stream().mapToDouble(t -> t.getShowSeat().getPrice()).sum());
        double convenienceFee = round(seatCost * 0.02);
        double gst = round(seatCost * 0.18); // User changed logic: 18% on Seat Price
        double originalTotal = round(seatCost + convenienceFee + gst);

        double finalAmount = originalTotal;
        String mode = paymentDTO.getPaymentMode();

        double discount = 0.0;
        if ("CREDIT_CARD".equalsIgnoreCase(mode)) {
            discount = round(originalTotal * 0.10);
            finalAmount = round(originalTotal - discount);
        } else if ("DEBIT_CARD".equalsIgnoreCase(mode)) {
            discount = round(originalTotal * 0.05);
            finalAmount = round(originalTotal - discount);
        }

        payment.setAmountPaid(finalAmount); // Override with backend calculated amount
        payment.setPaymentMode(mode);
        payment.setTransactionId(transactionId);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setBooking(booking);

        Payment savedPayment = paymentRepository.save(payment);

        PaymentDTO responseDTO = new PaymentDTO();
        responseDTO.setId(savedPayment.getId());
        responseDTO.setTransactionId(savedPayment.getTransactionId());
        responseDTO.setAmountPaid(savedPayment.getAmountPaid());
        responseDTO.setPaymentTime(savedPayment.getPaymentTime());
        responseDTO.setPaymentMode(savedPayment.getPaymentMode());
        responseDTO.setBookingId(savedPayment.getBooking().getId());

        // Send Email Async
        try {
            com.infy.icinema.dto.BookingDTO emailDTO = new com.infy.icinema.dto.BookingDTO();
            emailDTO.setId(booking.getId());
            emailDTO.setMovieTitle(booking.getShow().getMovie().getTitle());
            emailDTO.setTheatreName(booking.getShow().getScreen().getTheatre().getName());
            emailDTO.setCity(booking.getShow().getScreen().getTheatre().getCity());
            emailDTO.setShowDate(booking.getShow().getShowDate());
            emailDTO.setShowTime(booking.getShow().getShowTime());
            emailDTO.setTotalAmount(finalAmount); // Show actual paid amount
            emailDTO.setDiscountAmount(discount > 0.5 ? discount : 0.0); // Show discount if any
            emailDTO.setTransactionId(transactionId);
            emailDTO.setSeatCost(seatCost);
            emailDTO.setConvenienceFee(convenienceFee);
            emailDTO.setGst(gst);

            List<String> seats = tickets.stream()
                    .map(t -> t.getShowSeat().getSeat().getRowName() + t.getShowSeat().getSeat().getSeatNumber())
                    .collect(java.util.stream.Collectors.toList());
            emailDTO.setSeatNumbers(seats);

            emailService.sendBookingConfirmation(booking.getUser().getEmail(), emailDTO);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }

        return responseDTO;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
