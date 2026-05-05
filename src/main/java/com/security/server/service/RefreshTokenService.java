package com.security.server.service;

import com.security.server.entity.RefreshToken;
import com.security.server.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final long REFRESH_TOKEN_DURATION_DAYS = 7;

    @Transactional
    public RefreshToken createRefreshToken (Long userId) {
        log.info("Creating refresh token for userId: {}", userId);
        refreshTokenRepository.deleteByUserId(userId);

        RefreshToken token = new RefreshToken();
        token.setUserId(userId);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusDays(REFRESH_TOKEN_DURATION_DAYS));
        return refreshTokenRepository.save(token);
    }

    public RefreshToken verifyToken (String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            log.warn("Refresh token expired for userId: {}", refreshToken.getUserId());
            throw new RuntimeException("Refresh token expired");
        }
         return refreshToken;
    }

    @Transactional
    public void deleteByToken (String token) {
        log.info("Logout request for refresh token: {}", token);
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid Refresh token"));

        refreshTokenRepository.delete(refreshToken);
    }
}
