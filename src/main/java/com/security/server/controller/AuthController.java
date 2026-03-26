package com.security.server.controller;

import com.security.server.dto.LoginRequestDTO;
import com.security.server.dto.LoginResponseDTO;
import com.security.server.dto.RegisterRequestDTO;
import com.security.server.dto.RegisterResponseDTO;
import com.security.server.security.CustomUserDetails;
import com.security.server.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    @PostMapping("/register")
    public RegisterResponseDTO register(@RequestBody RegisterRequestDTO registerRequestDTO){
        authService.register(registerRequestDTO);
        return new RegisterResponseDTO("Registered successfully");
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getIdentifier(),
                        loginRequestDTO.getPassword()
                )
        );
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        assert userDetails != null;
        return new LoginResponseDTO("Login Successful", userDetails.getUsername());
    }
}
