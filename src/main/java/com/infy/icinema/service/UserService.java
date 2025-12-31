package com.infy.icinema.service;

import com.infy.icinema.dto.UserDTO;

public interface UserService {
    UserDTO registerUser(UserDTO userDTO);

    UserDTO loginUser(String email, String password);
}
