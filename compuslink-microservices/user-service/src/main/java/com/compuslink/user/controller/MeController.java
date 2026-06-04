package com.compuslink.user.controller;

import com.compuslink.user.dto.UpdateProfileRequest;
import com.compuslink.user.dto.UserProfileResponse;
import com.compuslink.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/me")
public class MeController {

    private final UserService userService;

    public MeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public UserProfileResponse getProfile(@RequestHeader("X-User-Id") UUID userId) {
        return userService.getProfile(userId);
    }

    @PatchMapping("/profile")
    public UserProfileResponse updateProfile(@RequestHeader("X-User-Id") UUID userId,
                                             @Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(userId, request);
    }
}
