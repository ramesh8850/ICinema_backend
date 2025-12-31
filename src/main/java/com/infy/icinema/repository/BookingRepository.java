package com.infy.icinema.repository;

import com.infy.icinema.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser_Id(Long userId);

    List<Booking> findByBookingStatusAndBookingDateBefore(String status, java.time.LocalDateTime dateTime);
}
