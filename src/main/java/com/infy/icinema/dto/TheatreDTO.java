package com.infy.icinema.dto;

import lombok.Data;

@Data
public class TheatreDTO {
    private Long id;

    @jakarta.validation.constraints.NotBlank(message = "Theatre name cannot be blank")
    private String name;

    @jakarta.validation.constraints.NotBlank(message = "City cannot be blank")
    private String city;

    @jakarta.validation.constraints.NotBlank(message = "Address cannot be blank")
    private String address;
}
