package com.infy.icinema.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;

    @jakarta.validation.constraints.NotBlank(message = "Username cannot be blank")
    private String username;

    @jakarta.validation.constraints.NotBlank(message = "Password cannot be blank")
    @jakarta.validation.constraints.Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @jakarta.validation.constraints.NotBlank(message = "Email cannot be blank")
    @jakarta.validation.constraints.Email(message = "Invalid email format")
    private String email;

    @jakarta.validation.constraints.NotBlank(message = "Mobile number cannot be blank")
    @jakarta.validation.constraints.Pattern(regexp = "^\\d{10}$", message = "Mobile number must be 10 digits")
    private String mobileNumber;
}
