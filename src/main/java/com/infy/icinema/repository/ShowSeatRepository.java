package com.infy.icinema.repository;

import com.infy.icinema.entity.ShowSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {
    java.util.List<ShowSeat> findByIdIn(java.util.List<Long> ids);

    java.util.List<ShowSeat> findByShow_Id(Long showId);
}
