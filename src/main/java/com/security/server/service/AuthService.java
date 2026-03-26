package com.security.server.service;

import com.security.server.dto.RegisterRequestDTO;
import com.security.server.entity.User;
import com.security.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequestDTO registerRequestDTO){
        //Check user exists by username
        if (userRepository.existsByUsername(registerRequestDTO.getUserName())){
            throw new RuntimeException("Username already exists");
        }

        //Check user exists by email
        if (userRepository.existsByEmail(registerRequestDTO.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        //Save user email, username and password
        User user = new User();
        user.setEmail(registerRequestDTO.getEmail());
        user.setUsername(registerRequestDTO.getUserName());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        userRepository.save(user);
    }
}
