package com.infy.icinema.service.impl;

import com.infy.icinema.dto.UserDTO;
import com.infy.icinema.entity.User;
import com.infy.icinema.exception.UserNotFoundException;
import com.infy.icinema.repository.UserRepository;
import com.infy.icinema.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        // Password encryption should be here
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public UserDTO loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        if (!user.getPassword().equals(password)) {
            throw new UserNotFoundException("Invalid credentials");
        }
        return modelMapper.map(user, UserDTO.class);
    }
}
