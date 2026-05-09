package com.CompusLink.CompusLink.domain.user.service;

import com.CompusLink.CompusLink.domain.user.dto.AuthResponse;
import com.CompusLink.CompusLink.domain.user.dto.LoginRequest;
import com.CompusLink.CompusLink.domain.user.dto.RefreshTokenRequest;
import com.CompusLink.CompusLink.domain.user.dto.RegisterRequest;
import com.CompusLink.CompusLink.domain.user.model.UserRole;
import com.CompusLink.CompusLink.domain.user.model.Users;
import com.CompusLink.CompusLink.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JWTService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public AuthResponse register(RegisterRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        Users user = new Users();
        user.setEmail(request.getEmail());
        user.setPasswordHash(encoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(request.getRole() == null ? UserRole.STUDENT : request.getRole());
        user.setUniversity(request.getUniversity());
        user.setCity(request.getCity());
        user.setPhoneNumber(request.getPhoneNumber());

        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        Users savedUser = storeRefreshToken(user, refreshToken);
        String token = jwtService.generateToken(savedUser.getEmail());

        return toAuthResponse(savedUser, token, refreshToken);
    }

    public AuthResponse verify(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        Users user = userRepo.findByEmail(request.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        Users savedUser = storeRefreshToken(user, refreshToken);
        String token = jwtService.generateToken(user.getEmail());

        return toAuthResponse(savedUser, token, refreshToken);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        Users user = userRepo.findByEmail(jwtService.extractUserName(refreshToken));
        if (user == null || user.getRefreshTokenHash() == null || !refreshTokenMatches(refreshToken, user.getRefreshTokenHash())) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        String accessToken = jwtService.generateToken(user.getEmail());
        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail());
        Users savedUser = storeRefreshToken(user, newRefreshToken);

        return toAuthResponse(savedUser, accessToken, newRefreshToken);
    }

    public void logout(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        Users user = userRepo.findByEmail(jwtService.extractUserName(refreshToken));
        if (user == null || user.getRefreshTokenHash() == null || !refreshTokenMatches(refreshToken, user.getRefreshTokenHash())) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        user.setRefreshTokenHash(null);
        userRepo.save(user);
    }

    private Users storeRefreshToken(Users user, String refreshToken) {
        user.setRefreshTokenHash(hashRefreshToken(refreshToken));
        return userRepo.save(user);
    }

    private boolean refreshTokenMatches(String refreshToken, String storedHash) {
        return MessageDigest.isEqual(
                hashRefreshToken(refreshToken).getBytes(StandardCharsets.UTF_8),
                storedHash.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String hashRefreshToken(String refreshToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available", ex);
        }
    }

    private AuthResponse toAuthResponse(Users user, String token, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
