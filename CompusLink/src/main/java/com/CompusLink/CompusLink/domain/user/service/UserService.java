package com.CompusLink.CompusLink.domain.user.service;

import com.CompusLink.CompusLink.domain.user.dto.AuthResponse;
import com.CompusLink.CompusLink.domain.user.dto.LoginRequest;
import com.CompusLink.CompusLink.domain.user.dto.PublicUserProfileResponse;
import com.CompusLink.CompusLink.domain.user.dto.RefreshTokenRequest;
import com.CompusLink.CompusLink.domain.user.dto.RegisterRequest;
import com.CompusLink.CompusLink.domain.user.dto.UpdateProfileRequest;
import com.CompusLink.CompusLink.domain.user.dto.UserProfileResponse;
import com.CompusLink.CompusLink.domain.user.model.UserRole;
import com.CompusLink.CompusLink.domain.user.model.Users;
import com.CompusLink.CompusLink.domain.user.repository.UserRepository;
import com.CompusLink.CompusLink.domain.user.util.SecurityUtils;
import com.CompusLink.CompusLink.exception.EntityNotFoundException;
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
import java.util.UUID;

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

    // ==================== Profile Management Methods ====================

    /**
     * Retrieves the complete profile of the currently authenticated user.
     *
     * @return UserProfileResponse containing all user information except sensitive data
     * @throws EntityNotFoundException if the user is not found
     */
    public UserProfileResponse getMyProfile() {
        UUID userId = SecurityUtils.getCurrentUserId();
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        return toUserProfileResponse(user);
    }

    /**
     * Updates the profile of the currently authenticated user.
     * Only non-null fields in the request are applied (PATCH semantics).
     * Email, password, role, and tokens cannot be modified through this method.
     *
     * @param request UpdateProfileRequest containing the fields to update
     * @return UserProfileResponse with the updated profile
     * @throws EntityNotFoundException if the user is not found
     */
    public UserProfileResponse updateMyProfile(UpdateProfileRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        // Apply only non-null fields (PATCH semantics)
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getUniversity() != null) {
            user.setUniversity(request.getUniversity());
        }
        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        Users updatedUser = userRepo.save(user);
        return toUserProfileResponse(updatedUser);
    }

    /**
     * Retrieves the public profile of another user.
     * Only returns non-sensitive, publicly visible information.
     *
     * @param userId UUID of the user whose profile to retrieve
     * @return PublicUserProfileResponse containing public information only
     * @throws EntityNotFoundException if the user doesn't exist or is deactivated
     */
    public PublicUserProfileResponse getPublicUserProfile(UUID userId) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        // Don't expose deactivated users to prevent user enumeration
        if (!user.isActive()) {
            throw new EntityNotFoundException("User", userId);
        }

        return toPublicUserProfileResponse(user);
    }

    /**
     * Updates the CV URL for a user.
     * Internal method called after a successful CV file upload.
     *
     * @param userId UUID of the user
     * @param cvUrl  URL to the uploaded CV file
     * @throws EntityNotFoundException if the user is not found
     */
    public void updateCvUrl(UUID userId, String cvUrl) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        user.setCvUrl(cvUrl);
        userRepo.save(user);
    }

    /**
     * Updates the profile picture URL for a user.
     * Internal method called after a successful profile picture upload.
     *
     * @param userId        UUID of the user
     * @param profilePicUrl URL to the uploaded profile picture
     * @throws EntityNotFoundException if the user is not found
     */
    public void updateProfilePicUrl(UUID userId, String profilePicUrl) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        user.setProfilePicUrl(profilePicUrl);
        userRepo.save(user);
    }

    // ==================== Private Helper Methods ====================

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

    /**
     * Converts a Users entity to a UserProfileResponse DTO.
     * Contains all user information except sensitive data (password hash, refresh token hash).
     */
    private UserProfileResponse toUserProfileResponse(Users user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .fullName(user.getFullName())
                .university(user.getUniversity())
                .city(user.getCity())
                .phoneNumber(user.getPhoneNumber())
                .bio(user.getBio())
                .profilePicUrl(user.getProfilePicUrl())
                .cvUrl(user.getCvUrl())
                .isActive(user.isActive())
                .isVerified(user.isVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Converts a Users entity to a PublicUserProfileResponse DTO.
     * Contains only non-sensitive, publicly visible information.
     */
    private PublicUserProfileResponse toPublicUserProfileResponse(Users user) {
        return PublicUserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .university(user.getUniversity())
                .city(user.getCity())
                .bio(user.getBio())
                .profilePicUrl(user.getProfilePicUrl())
                .role(user.getRole())
                .build();
    }
}
