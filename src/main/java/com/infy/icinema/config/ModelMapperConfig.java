package com.infy.icinema.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // modelMapper.typeMap(Show.class, ShowDTO.class)
        // .addMappings(mapper -> {
        // mapper.map(src -> src.getMovie().getTitle(), ShowDTO::setMovieTitle);
        // mapper.map(src -> src.getScreen().getScreenName(), ShowDTO::setScreenName);
        // mapper.map(src -> src.getScreen().getTheatre().getName(),
        // ShowDTO::setTheatreName);
        // mapper.map(src -> src.getScreen().getTheatre().getCity(),
        // ShowDTO::setTheatreCity);
        // });

        return modelMapper;
    }
}
