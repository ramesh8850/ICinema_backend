package com.infy.icinema.repository;

import com.infy.icinema.entity.ShowSeatPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShowSeatPriceRepository extends JpaRepository<ShowSeatPrice, Long> {
    List<ShowSeatPrice> findByShowId(Long showId);

    Optional<ShowSeatPrice> findByShowIdAndSeatTypeId(Long showId, Long seatTypeId);
}
