package com.infy.icinema.service.impl;

import com.infy.icinema.dto.BookingDTO;
import com.infy.icinema.entity.Booking;
import com.infy.icinema.entity.Show;
import com.infy.icinema.entity.User;

import com.infy.icinema.exception.ShowNotFoundException;
import com.infy.icinema.exception.UserNotFoundException;
import com.infy.icinema.repository.BookingRepository;
import com.infy.icinema.repository.ShowRepository;
import com.infy.icinema.repository.UserRepository;
import com.infy.icinema.service.BookingService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShowRepository showRepository;
    @Autowired
    private com.infy.icinema.repository.ShowSeatRepository showSeatRepository;
    @Autowired
    private com.infy.icinema.repository.TicketRepository ticketRepository;
    @Autowired
    private com.infy.icinema.repository.PaymentRepository paymentRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public BookingDTO createBooking(BookingDTO bookingDTO) {
        User user = userRepository.findById(bookingDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + bookingDTO.getUserId()));
        Show show = showRepository.findById(bookingDTO.getShowId())
                .orElseThrow(() -> new ShowNotFoundException("Show not found with id: " + bookingDTO.getShowId()));

        List<com.infy.icinema.entity.ShowSeat> showSeats = showSeatRepository.findByIdIn(bookingDTO.getShowSeatIds());

        if (showSeats.size() != bookingDTO.getShowSeatIds().size()) {
            throw new RuntimeException("Some seats were not found");
        }

        // Validate and Block seats and Calculate Cost
        double seatCost = 0.0;
        for (com.infy.icinema.entity.ShowSeat seat : showSeats) {
            if (!seat.getShow().getId().equals(show.getId())) {
                throw new RuntimeException("Seat does not belong to the selected show");
            }
            if (!"AVAILABLE".equals(seat.getStatus())) {
                throw new RuntimeException("Seat is not available: " + seat.getSeat().getSeatNumber());
            }
            seat.setStatus("BLOCKED");
            seatCost += seat.getPrice();
        }
        showSeatRepository.saveAll(showSeats);

        // Calculate Fees
        double convenienceFee = seatCost * 0.02; // 2%
        double gst = convenienceFee * 0.18; // 18% on Fee
        double totalAmount = seatCost + convenienceFee + gst;

        Booking booking = new Booking();
        booking.setBookingDate(LocalDateTime.now());
        booking.setTotalAmount(totalAmount);
        booking.setBookingStatus("PENDING");
        booking.setUser(user);
        booking.setShow(show);

        Booking savedBooking = bookingRepository.save(booking);

        // Create Tickets
        List<com.infy.icinema.entity.Ticket> tickets = showSeats.stream().map(seat -> {
            com.infy.icinema.entity.Ticket ticket = new com.infy.icinema.entity.Ticket();
            ticket.setBooking(savedBooking);
            ticket.setShowSeat(seat);
            return ticket;
        }).collect(Collectors.toList());
        ticketRepository.saveAll(tickets);

        BookingDTO responseDTO = modelMapper.map(savedBooking, BookingDTO.class);
        responseDTO.setSeatCost(seatCost);
        responseDTO.setConvenienceFee(convenienceFee);
        responseDTO.setGst(gst);

        return responseDTO;
    }

    @Override
    public List<BookingDTO> getBookingsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        return bookingRepository.findByUser_Id(userId).stream()
                .map(booking -> modelMapper.map(booking, BookingDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public com.infy.icinema.dto.TicketDTO getTicketDetails(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        com.infy.icinema.entity.Payment payment = paymentRepository.findByBooking_Id(bookingId)
                .orElseThrow(() -> new RuntimeException("Payment not found for booking id: " + bookingId));

        // Re-calculate cost breakdown (or store it in Booking if added later. Re-calc
        // is safe here)
        List<com.infy.icinema.entity.Ticket> tickets = ticketRepository.findByBookingId(bookingId);
        List<String> seatNumbers = tickets.stream()
                .map(t -> t.getShowSeat().getSeat().getRowName() + t.getShowSeat().getSeat().getSeatNumber())
                .collect(Collectors.toList());

        double seatCost = tickets.stream().mapToDouble(t -> t.getShowSeat().getPrice()).sum();
        double convenienceFee = seatCost * 0.02;
        double gst = convenienceFee * 0.18;

        com.infy.icinema.dto.TicketDTO dto = new com.infy.icinema.dto.TicketDTO();
        dto.setMovieTitle(booking.getShow().getMovie().getTitle());
        dto.setTheatreName(booking.getShow().getScreen().getTheatre().getName());
        dto.setCity(booking.getShow().getScreen().getTheatre().getCity());
        dto.setSeatNumbers(seatNumbers);
        dto.setCensorRating(booking.getShow().getMovie().getCensorRating());

        dto.setSeatCost(seatCost);
        dto.setConvenienceFee(convenienceFee);
        dto.setGst(gst);
        dto.setTotalAmount(booking.getTotalAmount());

        dto.setTransactionId(payment.getTransactionId());
        dto.setShowDate(booking.getShow().getShowDate());
        dto.setShowTime(booking.getShow().getShowTime());

        return dto;
    }

    @Override
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        // Only cancel if PENDING
        if ("PENDING".equals(booking.getBookingStatus())) {
            // Revert Seats
            List<com.infy.icinema.entity.Ticket> tickets = ticketRepository.findByBookingId(bookingId);
            for (com.infy.icinema.entity.Ticket ticket : tickets) {
                com.infy.icinema.entity.ShowSeat seat = ticket.getShowSeat();
                seat.setStatus("AVAILABLE");
                showSeatRepository.save(seat);
            }

            // Mark Booking Cancelled
            booking.setBookingStatus("CANCELLED");
            bookingRepository.save(booking);
        }
    }
}
