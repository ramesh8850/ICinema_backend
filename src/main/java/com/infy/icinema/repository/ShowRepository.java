package com.infy.icinema.repository;

import com.infy.icinema.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDate;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByMovie_Id(Long movieId);

    List<Show> findByScreen_Id(Long screenId);

    List<Show> findByMovie_IdAndShowDate(Long movieId, LocalDate showDate);
}
