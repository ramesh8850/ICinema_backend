package com.infy.icinema.service;

import com.infy.icinema.dto.ShowDTO;
import com.infy.icinema.dto.ShowSeatDTO;
import java.util.List;

public interface ShowService {
    List<ShowDTO> getShowsByMovie(Long movieId);

    ShowDTO addShow(ShowDTO showDTO);

    List<ShowSeatDTO> getShowSeats(Long showId);

    ShowDTO getShowById(Long showId);
}
