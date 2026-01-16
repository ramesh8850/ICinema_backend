package com.infy.icinema.service.impl;

import com.infy.icinema.dto.MovieDTO;
import com.infy.icinema.entity.Movie;
import com.infy.icinema.entity.User;
import com.infy.icinema.entity.Watchlist;
import com.infy.icinema.repository.MovieRepository;
import com.infy.icinema.repository.UserRepository;
import com.infy.icinema.repository.WatchlistRepository;
import com.infy.icinema.service.WatchlistService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WatchlistServiceImpl implements WatchlistService {

    @Autowired
    private WatchlistRepository watchlistRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void toggleWatchlist(Long userId, Long movieId) {
        if (watchlistRepository.existsByUser_IdAndMovie_Id(userId, movieId)) {
            watchlistRepository.deleteByUser_IdAndMovie_Id(userId, movieId);
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Movie movie = movieRepository.findById(movieId)
                    .orElseThrow(() -> new RuntimeException("Movie not found"));

            Watchlist watchlist = new Watchlist();
            watchlist.setUser(user);
            watchlist.setMovie(movie);
            watchlistRepository.save(watchlist);
        }
    }

    @Override
    public List<MovieDTO> getUserWatchlist(Long userId) {
        return watchlistRepository.findByUser_Id(userId).stream()
                .map(w -> modelMapper.map(w.getMovie(), MovieDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isWatchlisted(Long userId, Long movieId) {
        return watchlistRepository.existsByUser_IdAndMovie_Id(userId, movieId);
    }
}
