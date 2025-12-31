package com.infy.icinema.service.impl;

import com.infy.icinema.dto.TheatreDTO;
import com.infy.icinema.entity.Theatre;

import com.infy.icinema.repository.TheatreRepository;
import com.infy.icinema.service.TheatreService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TheatreServiceImpl implements TheatreService {
    @Autowired
    private TheatreRepository theatreRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<TheatreDTO> getAllTheatres() {
        return theatreRepository.findAll().stream()
                .map(theatre -> modelMapper.map(theatre, TheatreDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TheatreDTO> getTheatresByCity(String city) {
        return theatreRepository.findByCity(city).stream()
                .map(theatre -> modelMapper.map(theatre, TheatreDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public TheatreDTO addTheatre(TheatreDTO theatreDTO) {
        Theatre theatre = modelMapper.map(theatreDTO, Theatre.class);
        Theatre saved = theatreRepository.save(theatre);
        return modelMapper.map(saved, TheatreDTO.class);
    }
}
