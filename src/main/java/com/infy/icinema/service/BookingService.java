package com.infy.icinema.service;

import com.infy.icinema.dto.BookingDTO;
import java.util.List;

public interface BookingService {
    BookingDTO createBooking(BookingDTO bookingDTO);

    List<BookingDTO> getBookingsByUser(Long userId);

    com.infy.icinema.dto.TicketDTO getTicketDetails(Long bookingId);

    void cancelBooking(Long bookingId);
}
