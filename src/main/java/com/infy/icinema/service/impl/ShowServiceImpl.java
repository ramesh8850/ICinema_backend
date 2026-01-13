package com.infy.icinema.service.impl;

import com.infy.icinema.dto.ShowDTO;
import com.infy.icinema.dto.ShowSeatDTO;
import com.infy.icinema.entity.*;
import com.infy.icinema.exception.MovieNotFoundException;
import com.infy.icinema.exception.ScreenNotFoundException;
import com.infy.icinema.exception.ShowNotFoundException;
import com.infy.icinema.repository.*;
import com.infy.icinema.service.ShowService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShowServiceImpl implements ShowService {
    @Autowired
    private ShowRepository showRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ScreenRepository screenRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private ShowSeatRepository showSeatRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ShowSeatPriceRepository showSeatPriceRepository;
    @Autowired
    private SeatTypeRepository seatTypeRepository;

    @Override
    public List<ShowDTO> getShowsByMovie(Long movieId) {
        return showRepository.findByMovie_Id(movieId).stream()
                .map(show -> {
                    ShowDTO dto = modelMapper.map(show, ShowDTO.class);
                    // Manual mapping for display fields
                    if (show.getMovie() != null) {
                        dto.setMovieTitle(show.getMovie().getTitle());
                    }
                    if (show.getScreen() != null) {
                        dto.setScreenName(show.getScreen().getScreenName());
                        if (show.getScreen().getTheatre() != null) {
                            dto.setTheatreName(show.getScreen().getTheatre().getName());
                            dto.setTheatreCity(show.getScreen().getTheatre().getCity());
                        }
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ShowDTO addShow(ShowDTO showDTO) {
        Movie movie = movieRepository.findById(showDTO.getMovieId())
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + showDTO.getMovieId()));
        Screen screen = screenRepository.findById(showDTO.getScreenId())
                .orElseThrow(() -> new ScreenNotFoundException("Screen not found with id: " + showDTO.getScreenId()));

        Show show = modelMapper.map(showDTO, Show.class);
        show.setMovie(movie);
        show.setScreen(screen);

        Show savedShow = showRepository.save(show);

        // 1. Save Dynamic Prices
        // Assuming user sends "SILVER": 100 in DTO. If not, fallback defaults or throw
        // error.
        // For migration/safety, we ensure defaults if map is empty.
        java.util.Map<String, Double> prices = showDTO.getSeatPrices();
        if (prices == null || prices.isEmpty()) {
            prices = new java.util.HashMap<>();
            prices.put("SILVER", 150.0);
            prices.put("GOLD", 200.0);
            prices.put("PLATINUM", 250.0);
        }

        List<ShowSeatPrice> savedPrices = new ArrayList<>();
        for (java.util.Map.Entry<String, Double> entry : prices.entrySet()) {
            SeatType type = seatTypeRepository.findByName(entry.getKey()).orElse(null);
            if (type != null) {
                ShowSeatPrice priceEntity = new ShowSeatPrice();
                priceEntity.setShow(savedShow);
                priceEntity.setSeatType(type);
                priceEntity.setPrice(entry.getValue());
                savedPrices.add(showSeatPriceRepository.save(priceEntity));
            }
        }

        // 2. Generate ShowSeats - REMOVED (Sparse Storage)
        // We no longer pre-generate thousands of rows.
        // Rows are created only when a Booking is made.

        ShowDTO responseDTO = modelMapper.map(savedShow, ShowDTO.class);
        responseDTO.setSeatPrices(prices);
        return responseDTO;
    }

    @Override
    public List<ShowSeatDTO> getShowSeats(Long showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ShowNotFoundException("Show not found with id: " + showId));

        Screen screen = show.getScreen();

        // 1. Fetch all Physical Seats
        List<Seat> physicalSeats = seatRepository.findByScreenId(screen.getId());

        // 2. Fetch only BOOKED/BLOCKED seats (Sparse)
        List<ShowSeat> bookedSeats = showSeatRepository.findByShow_Id(showId);
        java.util.Map<Long, ShowSeat> bookingMap = bookedSeats.stream()
                .collect(Collectors.toMap(s -> s.getSeat().getId(), s -> s));

        // 3. Fetch Pricing Rules
        List<ShowSeatPrice> priceRules = showSeatPriceRepository.findByShowId(showId);
        java.util.Map<Long, Double> priceMap = priceRules.stream()
                .collect(Collectors.toMap(p -> p.getSeatType().getId(), ShowSeatPrice::getPrice));

        // 4. Merge Data
        return physicalSeats.stream().map(seat -> {
            ShowSeatDTO dto = new ShowSeatDTO();
            dto.setSeatId(seat.getId());
            dto.setRowName(seat.getRowName());
            dto.setSeatNumber(seat.getSeatNumber());
            dto.setSeatType(seat.getSeatType().getName());
            dto.setShowId(showId);

            // Check if Booked
            if (bookingMap.containsKey(seat.getId())) {
                ShowSeat booked = bookingMap.get(seat.getId());
                dto.setId(booked.getId());
                dto.setStatus(booked.getStatus());
                // Price is locked at booking time, or current price?
                // Usually current price for display, unless locked.
                dto.setPrice(booked.getPrice());
            } else {
                // It is Available
                dto.setId(null); // No ShowSeat ID yet
                dto.setStatus("AVAILABLE");
                // Calculate Price
                Double price = priceMap.getOrDefault(seat.getSeatType().getId(), 0.0);
                dto.setPrice(price);
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public ShowDTO getShowById(Long showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ShowNotFoundException(
                        "Show not found with id: " + showId));
        ShowDTO dto = modelMapper.map(show, ShowDTO.class);

        // Manual mapping for display fields
        if (show.getMovie() != null) {
            dto.setMovieTitle(show.getMovie().getTitle());
        }
        if (show.getScreen() != null) {
            dto.setScreenName(show.getScreen().getScreenName());
            if (show.getScreen().getTheatre() != null) {
                dto.setTheatreName(show.getScreen().getTheatre().getName());
                dto.setTheatreCity(show.getScreen().getTheatre().getCity());
            }
        }
        return dto;
    }
}
