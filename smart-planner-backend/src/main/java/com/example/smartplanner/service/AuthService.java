package com.example.smartplanner.service;

import com.example.smartplanner.dto.auth.*;
import com.example.smartplanner.entity.*;
import com.example.smartplanner.exception.BadRequestException;
import com.example.smartplanner.repository.*;
import com.example.smartplanner.security.JwtService;
import com.example.smartplanner.security.TokenHash;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final RefreshTokenRepository refreshRepo;
    private final TokenBlacklistRepository blacklistRepo;

    public AuthService(UserRepository users,
                       RoleRepository roles,
                       PasswordEncoder encoder,
                       JwtService jwt,
                       RefreshTokenRepository refreshRepo,
                       TokenBlacklistRepository blacklistRepo) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
        this.jwt = jwt;
        this.refreshRepo = refreshRepo;
        this.blacklistRepo = blacklistRepo;
    }

    private Role ensureRole(String name) {
        return roles.findByName(name).orElseGet(() -> roles.save(Role.builder().name(name).build()));
    }

    public AuthResponse register(RegisterRequest req) {
        String email = req.email().toLowerCase();
        if (users.existsByEmail(email)) throw new BadRequestException("Email already in use");

        // Ensure roles exist
        ensureRole("ADMIN");
        ensureRole("MANAGER");
        Role userRole = ensureRole("USER");

        User u = User.builder()
                .email(email)
                .passwordHash(encoder.encode(req.password()))
                .name(req.name())
                .surname(req.surname())
                .role(userRole)
                .build();
        u = users.save(u);

        JwtService.JwtPair pair = jwt.generatePair(u.getId(), u.getEmail(), u.getRole().getName());

        refreshRepo.save(RefreshToken.builder()
                .user(u)
                .tokenJti(pair.refreshJti())
                .tokenHash(TokenHash.sha256(pair.refreshToken()))
                .expiresAt(pair.refreshExpiresAt())
                .revoked(false)
                .build());

        return new AuthResponse(
                pair.accessToken(),
                pair.refreshToken(),
                pair.refreshExpiresAt(),
                u.getId(),
                u.getEmail(),
                u.getName(),
                u.getSurname(),
                u.getRole().getName(),
                u.getProfileImageUrl()
        );
    }

    public AuthResponse login(LoginRequest req) {
        String email = req.email().toLowerCase();

        User u = users.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        if (!encoder.matches(req.password(), u.getPasswordHash()))
            throw new BadRequestException("Invalid credentials");

        JwtService.JwtPair pair = jwt.generatePair(u.getId(), u.getEmail(), u.getRole().getName());

        refreshRepo.save(RefreshToken.builder()
                .user(u)
                .tokenJti(pair.refreshJti())
                .tokenHash(TokenHash.sha256(pair.refreshToken()))
                .expiresAt(pair.refreshExpiresAt())
                .revoked(false)
                .build());

        return new AuthResponse(
                pair.accessToken(),
                pair.refreshToken(),
                pair.refreshExpiresAt(),
                u.getId(),
                u.getEmail(),
                u.getName(),
                u.getSurname(),
                u.getRole().getName(),
                u.getProfileImageUrl()
        );
    }

    public AuthResponse refresh(RefreshRequest req) {
        Claims claims;
        try {
            claims = jwt.parse(req.refreshToken()).getBody();
        } catch (Exception e) {
            throw new BadRequestException("Invalid refresh token");
        }

        if (!jwt.isRefreshToken(claims)) throw new BadRequestException("Not a refresh token");

        String refreshJti = claims.getId();
        Long userId = Long.parseLong(claims.getSubject());

        RefreshToken stored = refreshRepo.findByTokenJti(refreshJti)
                .orElseThrow(() -> new BadRequestException("Refresh token not recognized"));

        if (stored.isRevoked()) throw new BadRequestException("Refresh token revoked");
        if (stored.getExpiresAt().isBefore(Instant.now())) throw new BadRequestException("Refresh token expired");

        String incomingHash = TokenHash.sha256(req.refreshToken());
        if (!incomingHash.equals(stored.getTokenHash())) {
            throw new BadRequestException("Refresh token mismatch");
        }

        User u = users.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        // rotation: revoke old refresh + issue new pair
        stored.setRevoked(true);
        refreshRepo.save(stored);

        JwtService.JwtPair pair = jwt.generatePair(u.getId(), u.getEmail(), u.getRole().getName());

        refreshRepo.save(RefreshToken.builder()
                .user(u)
                .tokenJti(pair.refreshJti())
                .tokenHash(TokenHash.sha256(pair.refreshToken()))
                .expiresAt(pair.refreshExpiresAt())
                .revoked(false)
                .build());

        return new AuthResponse(
                pair.accessToken(),
                pair.refreshToken(),
                pair.refreshExpiresAt(),
                u.getId(),
                u.getEmail(),
                u.getName(),
                u.getSurname(),
                u.getRole().getName(),
                u.getProfileImageUrl()
        );
    }

    public void logout(String accessToken) {
        Claims claims;
        try {
            claims = jwt.parse(accessToken).getBody();
        } catch (Exception e) {
            return; // already invalid
        }

        if (!jwt.isAccessToken(claims)) return;

        String accessJti = claims.getId();
        Instant exp = claims.getExpiration().toInstant();
        Long userId = Long.parseLong(claims.getSubject());

        if (accessJti != null && !blacklistRepo.existsByTokenJti(accessJti)) {
            blacklistRepo.save(TokenBlacklist.builder()
                    .tokenJti(accessJti)
                    .expiresAt(exp)
                    .build());
        }

        // global logout: revoke all active refresh tokens
        refreshRepo.findByUserIdAndRevokedFalse(userId).forEach(rt -> {
            rt.setRevoked(true);
            refreshRepo.save(rt);
        });
    }
}
