package com.infy.icinema.service.impl;

import com.infy.icinema.dto.ShowDTO;
import com.infy.icinema.entity.*;
import com.infy.icinema.exception.MovieNotFoundException;
import com.infy.icinema.exception.ScreenNotFoundException;
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

    @Override
    public List<ShowDTO> getShowsByMovie(Long movieId) {
        return showRepository.findByMovie_Id(movieId).stream()
                .map(show -> modelMapper.map(show, ShowDTO.class))
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

        // Generate ShowSeats
        List<Seat> seats = seatRepository.findByScreenId(screen.getId());
        List<ShowSeat> showSeats = new ArrayList<>();

        for (Seat seat : seats) {
            ShowSeat showSeat = new ShowSeat();
            showSeat.setShow(savedShow);
            showSeat.setSeat(seat);
            showSeat.setStatus("AVAILABLE");

            if ("SILVER".equalsIgnoreCase(seat.getSeatType())) {
                showSeat.setPrice(savedShow.getPriceSilver());
            } else if ("GOLD".equalsIgnoreCase(seat.getSeatType())) {
                showSeat.setPrice(savedShow.getPriceGold());
            } else if ("PLATINUM".equalsIgnoreCase(seat.getSeatType())) {
                showSeat.setPrice(savedShow.getPricePlatinum());
            } else {
                showSeat.setPrice(savedShow.getPriceSilver());
            }
            showSeats.add(showSeat);
        }
        showSeatRepository.saveAll(showSeats);

        return modelMapper.map(savedShow, ShowDTO.class);
    }

    @Override
    public List<com.infy.icinema.dto.ShowSeatDTO> getShowSeats(Long showId) {
        if (!showRepository.existsById(showId)) {
            throw new com.infy.icinema.exception.ShowNotFoundException("Show not found with id: " + showId);
        }
        return showSeatRepository.findByShow_Id(showId).stream()
                .map(seat -> {
                    com.infy.icinema.dto.ShowSeatDTO dto = new com.infy.icinema.dto.ShowSeatDTO();
                    dto.setId(seat.getId());
                    dto.setStatus(seat.getStatus());
                    dto.setPrice(seat.getPrice());
                    dto.setShowId(seat.getShow().getId());
                    dto.setSeatId(seat.getSeat().getId());
                    dto.setRowName(seat.getSeat().getRowName());
                    dto.setSeatNumber(seat.getSeat().getSeatNumber());
                    dto.setSeatType(seat.getSeat().getSeatType());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ShowDTO getShowById(Long showId) {
        com.infy.icinema.entity.Show show = showRepository.findById(showId)
                .orElseThrow(() -> new com.infy.icinema.exception.ShowNotFoundException(
                        "Show not found with id: " + showId));
        return modelMapper.map(show, ShowDTO.class);
    }
}
