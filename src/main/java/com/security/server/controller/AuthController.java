package com.security.server.controller;

import com.security.server.dto.*;
import com.security.server.entity.RefreshToken;
import com.security.server.security.CustomUserDetails;
import com.security.server.security.JwtService;
import com.security.server.service.AuthService;
import com.security.server.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public RegisterResponseDTO register(@RequestBody RegisterRequestDTO registerRequestDTO){
        authService.register(registerRequestDTO);
        return new RegisterResponseDTO("Registered successfully");
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO){
        log.info("Login attempt for identifier: {}", loginRequestDTO.getIdentifier());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getIdentifier(),
                        loginRequestDTO.getPassword()
                )
        );
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        log.info("Login successful for user id: {}", userDetails.getUserId());
        String accessToken = jwtService.generateToken(userDetails.getUserId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUserId());
        return new LoginResponseDTO(accessToken, refreshToken.getToken());
    }

    @PostMapping("/refresh")
    public LoginResponseDTO refresh(@RequestBody RefreshTokenRequest request) {

        RefreshToken oldToken = refreshTokenService.verifyToken(request.getRefreshToken());

        Long userId = oldToken.getUserId();

        refreshTokenService.deleteByToken(request.getRefreshToken());

        String newAccessToken = jwtService.generateToken(userId);

        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(userId);

        return new LoginResponseDTO(
                newAccessToken,
                newRefreshToken.getToken()
        );
    }

    @PostMapping("/logout")
    public String logout(@RequestBody LogoutRequestDTO request) {

        refreshTokenService.deleteByToken(request.getRefreshToken());

        return "Logged out successfully";
    }
}
