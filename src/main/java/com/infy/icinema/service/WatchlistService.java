package com.infy.icinema.service;

import com.infy.icinema.dto.MovieDTO;
import java.util.List;

public interface WatchlistService {
    void toggleWatchlist(Long userId, Long movieId);

    List<MovieDTO> getUserWatchlist(Long userId);

    boolean isWatchlisted(Long userId, Long movieId);
}
