package com.infy.icinema.repository;

import com.infy.icinema.entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    List<Watchlist> findByUser_Id(Long userId);

    Optional<Watchlist> findByUser_IdAndMovie_Id(Long userId, Long movieId);

    boolean existsByUser_IdAndMovie_Id(Long userId, Long movieId);

    void deleteByUser_IdAndMovie_Id(Long userId, Long movieId);
}
