package com.infy.icinema.scheduler;

import com.infy.icinema.entity.Booking;
import com.infy.icinema.repository.BookingRepository;
import com.infy.icinema.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class BookingScheduler {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingService bookingService;

    // Run every 1 minute (60,000 ms)
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void convertPendingToFreed() {
        // Expiry time: 1 minute ago (Accelerated for testing/user request)
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(1);

        List<Booking> expiredBookings = bookingRepository.findByBookingStatusAndBookingDateBefore("PENDING",
                expirationTime);

        if (!expiredBookings.isEmpty()) {
            System.out.println("Found " + expiredBookings.size() + " expired pending bookings. Cleaning up...");
            for (Booking booking : expiredBookings) {
                try {
                    bookingService.cancelBooking(booking.getId());
                    System.out.println("Auto-cancelled expired booking ID: " + booking.getId());
                } catch (Exception e) {
                    System.err.println("Failed to auto-cancel booking ID: " + booking.getId() + " - " + e.getMessage());
                }
            }
        }
    }
}
