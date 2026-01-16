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
    @Autowired
    private com.infy.icinema.repository.SeatRepository seatRepository;
    @Autowired
    private com.infy.icinema.repository.ShowSeatPriceRepository showSeatPriceRepository;
    @Autowired
    private org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;
    @Autowired
    private com.infy.icinema.service.impl.TicketPdfService ticketPdfService; // Injected for secure QR

    @Override
    public BookingDTO createBooking(BookingDTO bookingDTO) {
        User user = userRepository.findById(bookingDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + bookingDTO.getUserId()));
        Show show = showRepository.findById(bookingDTO.getShowId())
                .orElseThrow(() -> new ShowNotFoundException("Show not found with id: " + bookingDTO.getShowId()));

        List<Long> physicalSeatIds = bookingDTO.getShowSeatIds();
        List<com.infy.icinema.entity.ShowSeat> showSeats = new java.util.ArrayList<>();
        double seatCost = 0.0;

        for (Long seatId : physicalSeatIds) {
            // Check if ShowSeat already exists (Sparse Storage)
            // We use a final variable or effective final for lambda, but here we are in a
            // loop so it's fine to use instance variables
            com.infy.icinema.entity.ShowSeat showSeat = showSeatRepository.findByShow_IdAndSeat_Id(show.getId(), seatId)
                    .orElse(null);

            if (showSeat == null) {
                // Create valid ShowSeat if not exists
                com.infy.icinema.entity.Seat physicalSeat = seatRepository.findById(seatId)
                        .orElseThrow(() -> new RuntimeException("Seat not found: " + seatId));

                showSeat = new com.infy.icinema.entity.ShowSeat();
                showSeat.setShow(show);
                showSeat.setSeat(physicalSeat);
                showSeat.setStatus("AVAILABLE");

                // Fetch and set price initially using injected repository
                com.infy.icinema.entity.ShowSeatPrice priceRule = showSeatPriceRepository
                        .findByShowIdAndSeatTypeId(show.getId(), physicalSeat.getSeatType().getId())
                        .orElse(null);

                if (priceRule == null) {
                    // Here physicalSeat is arguably effectively final as it's defined inside the if
                    // block,
                    // but to be consistent and safe, we use the extracted logic.
                    throw new RuntimeException(
                            "Price not defined for seat type: " + physicalSeat.getSeatType().getName());
                }
                showSeat.setPrice(priceRule.getPrice());

                showSeat = showSeatRepository.save(showSeat);
            }

            if (!"AVAILABLE".equals(showSeat.getStatus())) {
                throw new RuntimeException("Seat is not available: " + showSeat.getSeat().getSeatNumber());
            }
            showSeat.setStatus("BLOCKED");

            // Ensure price is set if missing (legacy/migration)
            if (showSeat.getPrice() == null) {
                com.infy.icinema.entity.ShowSeatPrice priceRule = showSeatPriceRepository
                        .findByShowIdAndSeatTypeId(show.getId(), showSeat.getSeat().getSeatType().getId())
                        .orElse(null);

                if (priceRule == null) {
                    throw new RuntimeException(
                            "Price not defined for seat type: " + showSeat.getSeat().getSeatType().getName());
                }
                showSeat.setPrice(priceRule.getPrice());
            }

            seatCost += showSeat.getPrice();
            showSeats.add(showSeat);
        }

        showSeatRepository.saveAll(showSeats);

        // Calculate Fees
        double convenienceFee = seatCost * 0.02; // 2%
        double gst = seatCost * 0.18; // 18% on Fee
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

        // Broadcast Real-Time Updates
        broadcastSeatUpdates(show.getId(), showSeats, user.getId());

        return responseDTO;
    }

    private void broadcastSeatUpdates(Long showId, List<com.infy.icinema.entity.ShowSeat> showSeats, Long userId) {
        for (com.infy.icinema.entity.ShowSeat seat : showSeats) {
            com.infy.icinema.dto.SeatUpdateDTO update = new com.infy.icinema.dto.SeatUpdateDTO(
                    showId,
                    seat.getSeat().getId(),
                    seat.getStatus(),
                    "Seat " + seat.getSeat().getSeatNumber() + " is " + seat.getStatus(),
                    userId);

            // Send to topic: /topic/seat-updates/{showId}
            messagingTemplate.convertAndSend("/topic/seat-updates/" + showId, update);
        }
    }

    @Override
    public List<BookingDTO> getBookingsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        return bookingRepository.findByUser_Id(userId).stream()
                .map(booking -> {
                    BookingDTO dto = modelMapper.map(booking, BookingDTO.class);
                    // Manually populate derived fields
                    dto.setMovieTitle(booking.getShow().getMovie().getTitle());
                    dto.setTheatreName(booking.getShow().getScreen().getTheatre().getName());
                    dto.setCity(booking.getShow().getScreen().getTheatre().getCity());
                    dto.setShowDate(booking.getShow().getShowDate());
                    dto.setShowTime(booking.getShow().getShowTime());

                    // Fetch Seat Numbers
                    List<com.infy.icinema.entity.Ticket> tickets = ticketRepository.findByBookingId(booking.getId());
                    List<String> seatNumbers = tickets.stream()
                            .map(t -> t.getShowSeat().getSeat().getRowName()
                                    + t.getShowSeat().getSeat().getSeatNumber())
                            .collect(Collectors.toList());
                    dto.setSeatNumbers(seatNumbers);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    @Override
    public com.infy.icinema.dto.TicketDTO getTicketDetails(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        com.infy.icinema.entity.Payment payment = paymentRepository.findByBooking_Id(bookingId)
                .orElseThrow(() -> new RuntimeException("Payment not found for booking id: " + bookingId));

        // Re-calculate cost breakdown
        List<com.infy.icinema.entity.Ticket> tickets = ticketRepository.findByBookingId(bookingId);
        List<String> seatNumbers = tickets.stream()
                .map(t -> t.getShowSeat().getSeat().getRowName() + t.getShowSeat().getSeat().getSeatNumber())
                .collect(Collectors.toList());

        // Fetch Seat Types
        String seatTypes = tickets.stream()
                .map(t -> t.getShowSeat().getSeat().getSeatType().getName())
                .distinct()
                .collect(Collectors.joining(", "));

        double seatCost = round(tickets.stream().mapToDouble(t -> t.getShowSeat().getPrice()).sum());
        double convenienceFee = round(seatCost * 0.02);
        double gst = round(seatCost * 0.18);

        com.infy.icinema.dto.TicketDTO dto = new com.infy.icinema.dto.TicketDTO();
        dto.setMovieTitle(booking.getShow().getMovie().getTitle());
        dto.setMoviePosterUrl(booking.getShow().getMovie().getImageUrl());

        // Secure QR Payload
        try {
            String securePayload = ticketPdfService.generateSecurePayload(booking.getId());
            dto.setQrPayload(securePayload);
        } catch (Exception e) {
            // Fallback or log error
            dto.setQrPayload("d2aa18b2-1dc2-4791-8645-f6a5f8298bf9: " + booking.getId());
        }
        dto.setTheatreName(booking.getShow().getScreen().getTheatre().getName());
        dto.setScreenName(booking.getShow().getScreen().getScreenName());
        dto.setCity(booking.getShow().getScreen().getTheatre().getCity());
        dto.setSeatNumbers(seatNumbers);
        dto.setSeatType(seatTypes); // Set Seat Type
        dto.setCensorRating(booking.getShow().getMovie().getCensorRating());

        dto.setSeatCost(seatCost);
        dto.setConvenienceFee(convenienceFee);
        dto.setGst(gst);

        // Calculate original total to find discount
        double originalTotal = round(seatCost + convenienceFee + gst);
        double actualPaid = round(payment.getAmountPaid());
        double discount = round(originalTotal - actualPaid);

        if (discount > 0.5) { // Threshold for floating point diff
            dto.setDiscountAmount(discount);
        } else {
            dto.setDiscountAmount(0.0);
        }

        dto.setTotalAmount(actualPaid);

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
