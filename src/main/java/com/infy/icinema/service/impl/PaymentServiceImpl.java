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

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    @Override
    public PaymentDTO makePayment(PaymentDTO paymentDTO) {
        Booking booking = bookingRepository.findById(paymentDTO.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + paymentDTO.getBookingId()));

        if (!"PENDING".equals(booking.getBookingStatus())) {
            throw new RuntimeException("Booking is already confirmed or invalid status: " + booking.getBookingStatus());
        }

        // Generate unique Transaction ID
        String transactionId = "TXN_"
                + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

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
        payment.setAmountPaid(paymentDTO.getAmountPaid());
        payment.setPaymentMode(paymentDTO.getPaymentMode());
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

        return responseDTO;
    }
}
