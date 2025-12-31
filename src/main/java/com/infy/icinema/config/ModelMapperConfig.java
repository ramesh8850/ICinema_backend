package com.infy.icinema.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(com.infy.icinema.entity.Show.class, com.infy.icinema.dto.ShowDTO.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getMovie().getTitle(), com.infy.icinema.dto.ShowDTO::setMovieTitle);
                    mapper.map(src -> src.getScreen().getScreenName(), com.infy.icinema.dto.ShowDTO::setScreenName);
                    mapper.map(src -> src.getScreen().getTheatre().getName(),
                            com.infy.icinema.dto.ShowDTO::setTheatreName);
                    mapper.map(src -> src.getScreen().getTheatre().getCity(),
                            com.infy.icinema.dto.ShowDTO::setTheatreCity);
                });

        return modelMapper;
    }
}
