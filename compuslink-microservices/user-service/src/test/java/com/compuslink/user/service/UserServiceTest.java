package com.compuslink.user.service;

import com.compuslink.common.exception.EntityNotFoundException;
import com.compuslink.user.dto.*;
import com.compuslink.user.model.UserRole;
import com.compuslink.user.model.Users;
import com.compuslink.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTService jwtService;

    @Mock
    private UserFileStorageService fileStorageService;

    @InjectMocks
    private UserService userService;

    private Users testUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = new Users();
        testUser.setId(testUserId);
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setRole(UserRole.STUDENT);
        testUser.setActive(true);
    }

    @Test
    void testRegisterUserSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@example.com");
        request.setPassword("password123");
        request.setFullName("New User");
        request.setRole(UserRole.STUDENT);
        request.setUniversity("Test University");
        request.setCity("Test City");
        request.setPhoneNumber("1234567890");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(jwtService.generateRefreshToken(anyString())).thenReturn("refresh-token");
        when(jwtService.generateToken(anyString())).thenReturn("access-token");
        when(userRepository.save(any(Users.class))).thenReturn(testUser);

        AuthResponse response = userService.register(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        verify(userRepository).save(any(Users.class));
    }

    @Test
    void testRegisterUserEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.register(request));
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    void testVerifyUserSuccess() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        when(userRepository.findByEmail(request.getEmail())).thenReturn(testUser);
        when(jwtService.generateRefreshToken(anyString())).thenReturn("refresh-token");
        when(jwtService.generateToken(anyString())).thenReturn("access-token");
        when(userRepository.save(any(Users.class))).thenReturn(testUser);

        AuthResponse response = userService.verify(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(testUserId, response.getUserId());
    }

    @Test
    void testRefreshTokenInvalid() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("invalid-token");

        when(jwtService.validateRefreshToken("invalid-token")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> userService.refreshToken(request));
    }

    @Test
    void testGetProfileSuccess() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        UserProfileResponse response = userService.getProfile(testUserId);

        assertNotNull(response);
        assertEquals(testUserId, response.getId());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getFullName());
    }

    @Test
    void testGetProfileUserNotFound() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getProfile(testUserId));
    }

    @Test
    void testUpdateProfileSuccess() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName("Updated User");
        request.setCity("New City");
        request.setBio("New Bio");

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(Users.class))).thenReturn(testUser);

        UserProfileResponse response = userService.updateProfile(testUserId, request);

        assertNotNull(response);
        verify(userRepository).save(any(Users.class));
    }

    @Test
    void testGetPublicProfileSuccess() {
        testUser.setActive(true);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        PublicUserProfileResponse response = userService.getPublicProfile(testUserId);

        assertNotNull(response);
        assertEquals(testUserId, response.getId());
        assertEquals("Test User", response.getFullName());
    }

    @Test
    void testGetPublicProfileInactiveUser() {
        testUser.setActive(false);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        assertThrows(EntityNotFoundException.class, () -> userService.getPublicProfile(testUserId));
    }


    @Test
    void testExistsByIdTrue() {
        when(userRepository.existsById(testUserId)).thenReturn(true);

        boolean exists = userService.existsById(testUserId);

        assertTrue(exists);
    }

    @Test
    void testExistsByIdFalse() {
        when(userRepository.existsById(testUserId)).thenReturn(false);

        boolean exists = userService.existsById(testUserId);

        assertFalse(exists);
    }
}
