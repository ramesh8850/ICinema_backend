package com.infy.icinema.service;

import com.infy.icinema.dto.ShowDTO;
import java.util.List;

public interface ShowService {
    List<ShowDTO> getShowsByMovie(Long movieId);

    ShowDTO addShow(ShowDTO showDTO);

    java.util.List<com.infy.icinema.dto.ShowSeatDTO> getShowSeats(Long showId);

    ShowDTO getShowById(Long showId);
}
