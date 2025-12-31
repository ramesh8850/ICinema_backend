package com.infy.icinema.service;

import com.infy.icinema.dto.TheatreDTO;
import java.util.List;

public interface TheatreService {
    List<TheatreDTO> getAllTheatres();

    List<TheatreDTO> getTheatresByCity(String city);

    TheatreDTO addTheatre(TheatreDTO theatreDTO);
}
