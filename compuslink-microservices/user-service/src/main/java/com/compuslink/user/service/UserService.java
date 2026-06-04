package com.compuslink.user.service;

import com.compuslink.common.dto.UserSummaryDTO;
import com.compuslink.common.exception.EntityNotFoundException;
import com.compuslink.user.dto.*;
import com.compuslink.user.model.UserRole;
import com.compuslink.user.model.Users;
import com.compuslink.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepo;
    private final JWTService jwtService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public UserService(AuthenticationManager authenticationManager, UserRepository userRepo, JWTService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepo = userRepo;
        this.jwtService = jwtService;
    }

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
        Users saved = storeRefreshToken(user, refreshToken);
        String token = jwtService.generateToken(saved.getEmail());
        return toAuthResponse(saved, token, refreshToken);
    }

    public AuthResponse verify(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        Users user = userRepo.findByEmail(request.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        Users saved = storeRefreshToken(user, refreshToken);
        String token = jwtService.generateToken(user.getEmail());
        return toAuthResponse(saved, token, refreshToken);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        Users user = userRepo.findByEmail(jwtService.extractUserName(refreshToken));
        if (user == null || !refreshTokenMatches(refreshToken, user.getRefreshTokenHash())) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        String accessToken = jwtService.generateToken(user.getEmail());
        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail());
        Users saved = storeRefreshToken(user, newRefreshToken);
        return toAuthResponse(saved, accessToken, newRefreshToken);
    }

    public void logout(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        Users user = userRepo.findByEmail(jwtService.extractUserName(refreshToken));
        if (user == null || !refreshTokenMatches(refreshToken, user.getRefreshTokenHash())) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        user.setRefreshTokenHash(null);
        userRepo.save(user);
    }

    public UserProfileResponse getProfile(UUID userId) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return toProfileResponse(user);
    }

    public UserProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getUniversity() != null) user.setUniversity(request.getUniversity());
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());

        return toProfileResponse(userRepo.save(user));
    }

    public PublicUserProfileResponse getPublicProfile(UUID userId) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (!user.isActive()) throw new EntityNotFoundException("User not found");

        return PublicUserProfileResponse.builder()
                .id(user.getId()).fullName(user.getFullName())
                .university(user.getUniversity()).city(user.getCity())
                .bio(user.getBio()).profilePicUrl(user.getProfilePicUrl())
                .role(user.getRole()).build();
    }

    // Internal endpoint for other services to get user summary
    public UserSummaryDTO getUserSummary(UUID userId) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return new UserSummaryDTO(user.getId(), user.getFullName(), user.getEmail(), user.getProfilePicUrl());
    }

    public boolean existsById(UUID userId) {
        return userRepo.existsById(userId);
    }

    // --- helpers ---

    private Users storeRefreshToken(Users user, String refreshToken) {
        user.setRefreshTokenHash(hashToken(refreshToken));
        return userRepo.save(user);
    }

    private boolean refreshTokenMatches(String token, String storedHash) {
        if (storedHash == null) return false;
        return MessageDigest.isEqual(
                hashToken(token).getBytes(StandardCharsets.UTF_8),
                storedHash.getBytes(StandardCharsets.UTF_8));
    }

    private String hashToken(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private AuthResponse toAuthResponse(Users user, String token, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(token).refreshToken(refreshToken)
                .userId(user.getId()).email(user.getEmail()).role(user.getRole())
                .build();
    }

    private UserProfileResponse toProfileResponse(Users user) {
        return UserProfileResponse.builder()
                .id(user.getId()).email(user.getEmail()).role(user.getRole())
                .fullName(user.getFullName()).university(user.getUniversity())
                .city(user.getCity()).phoneNumber(user.getPhoneNumber())
                .bio(user.getBio()).profilePicUrl(user.getProfilePicUrl())
                .cvUrl(user.getCvUrl()).isActive(user.isActive())
                .isVerified(user.isVerified())
                .createdAt(user.getCreatedAt()).updatedAt(user.getUpdatedAt())
                .build();
    }
}
