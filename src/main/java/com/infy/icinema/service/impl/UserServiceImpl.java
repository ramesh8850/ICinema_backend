package com.infy.icinema.service.impl;

import com.infy.icinema.dto.UserDTO;
import com.infy.icinema.entity.User;
import com.infy.icinema.exception.UserNotFoundException;
import com.infy.icinema.repository.UserRepository;
import com.infy.icinema.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.infy.icinema.repository.RoleRepository roleRepository;

    public UserDTO registerUser(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assign default role: ROLE_USER
        com.infy.icinema.entity.Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        java.util.Set<com.infy.icinema.entity.Role> roles = new java.util.HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        UserDTO responseDTO = modelMapper.map(savedUser, UserDTO.class);
        responseDTO.setRoles(savedUser.getRoles().stream().map(com.infy.icinema.entity.Role::getName)
                .collect(java.util.stream.Collectors.toSet()));
        return responseDTO;
    }

    @Override
    public UserDTO loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        System.out.println("DEBUG LOGIN: Email=" + email);
        System.out.println("DEBUG LOGIN: InputPass='" + password + "'");
        System.out.println("DEBUG LOGIN: StoredPass='" + user.getPassword() + "'");

        if (!passwordEncoder.matches(password, user.getPassword())) {
            System.out.println("DEBUG: Mismatch! The correct hash for input '" + password + "' should be: "
                    + passwordEncoder.encode(password));
            throw new UserNotFoundException("Invalid credentials");
        }
        UserDTO responseDTO = modelMapper.map(user, UserDTO.class);
        responseDTO.setRoles(user.getRoles().stream().map(com.infy.icinema.entity.Role::getName)
                .collect(java.util.stream.Collectors.toSet()));

        System.out.println("DEBUG LOGIN: Returning UserDTO with roles: " + responseDTO.getRoles());

        return responseDTO;
    }
}
