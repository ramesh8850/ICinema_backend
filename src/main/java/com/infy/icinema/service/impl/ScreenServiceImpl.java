package com.infy.icinema.service.impl;

import com.infy.icinema.dto.ScreenDTO;
import com.infy.icinema.entity.Screen;
import com.infy.icinema.entity.Seat;
import com.infy.icinema.entity.Theatre;
import com.infy.icinema.exception.TheatreNotFoundException;
import com.infy.icinema.repository.ScreenRepository;
import com.infy.icinema.repository.SeatRepository;
import com.infy.icinema.repository.TheatreRepository;
import com.infy.icinema.service.ScreenService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ScreenServiceImpl implements ScreenService {

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private TheatreRepository theatreRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ScreenDTO addScreen(ScreenDTO screenDTO) {
        Theatre theatre = theatreRepository.findById(screenDTO.getTheatreId())
                .orElseThrow(
                        () -> new TheatreNotFoundException("Theatre not found with id: " + screenDTO.getTheatreId()));

        Screen screen = modelMapper.map(screenDTO, Screen.class);
        screen.setTheatre(theatre);

        Screen savedScreen = screenRepository.save(screen);

        generateSeats(savedScreen);

        return modelMapper.map(savedScreen, ScreenDTO.class);
    }

    @Override
    public List<ScreenDTO> getScreensByTheatre(Long theatreId) {
        List<Screen> screens = screenRepository.findByTheatreId(theatreId);
        return screens.stream()
                .map(screen -> modelMapper.map(screen, ScreenDTO.class))
                .collect(java.util.stream.Collectors.toList());
    }

    @Autowired
    private com.infy.icinema.repository.SeatTypeRepository seatTypeRepository;

    private void generateSeats(Screen screen) {
        int totalSeats = screen.getTotalSeats();
        int columns = 10;
        int rows = totalSeats / columns;
        if (totalSeats % columns != 0) {
            rows++;
        }

        // Cache or Create SeatTypes
        com.infy.icinema.entity.SeatType silver = getOrCreateSeatType("SILVER");
        com.infy.icinema.entity.SeatType gold = getOrCreateSeatType("GOLD");
        com.infy.icinema.entity.SeatType platinum = getOrCreateSeatType("PLATINUM");

        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            char rowChar = (char) ('A' + i);
            String rowName = String.valueOf(rowChar);

            com.infy.icinema.entity.SeatType seatType;
            if (i < 3) {
                seatType = silver;
            } else if (i < 7) {
                seatType = gold;
            } else {
                seatType = platinum;
            }

            for (int j = 1; j <= columns; j++) {
                if (seats.size() >= totalSeats)
                    break; // Stop if we reached totalSeats

                Seat seat = new Seat();
                seat.setRowName(rowName);
                seat.setSeatNumber(j);
                seat.setSeatType(seatType);
                seat.setScreen(screen);
                seats.add(seat);
            }
        }
        seatRepository.saveAll(seats);
    }

    private com.infy.icinema.entity.SeatType getOrCreateSeatType(String name) {
        return seatTypeRepository.findByName(name)
                .orElseGet(() -> {
                    com.infy.icinema.entity.SeatType newType = new com.infy.icinema.entity.SeatType();
                    newType.setName(name);
                    newType.setDescription(name + " Class Seat");
                    return seatTypeRepository.save(newType);
                });
    }
}
