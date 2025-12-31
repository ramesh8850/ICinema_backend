package com.infy.icinema.repository;

import com.infy.icinema.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    java.util.List<Seat> findByScreenId(Long screenId);
}
