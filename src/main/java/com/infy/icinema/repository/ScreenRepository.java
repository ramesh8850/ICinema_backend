package com.infy.icinema.repository;

import com.infy.icinema.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {
    java.util.List<Screen> findByTheatreId(Long theatreId);
}
