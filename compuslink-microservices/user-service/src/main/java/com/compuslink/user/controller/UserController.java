package com.compuslink.user.controller;

import com.compuslink.common.dto.UserSummaryDTO;
import com.compuslink.user.dto.PublicUserProfileResponse;
import com.compuslink.user.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/users/{id}/public")
    public PublicUserProfileResponse getPublicProfile(@PathVariable UUID id) {
        return userService.getPublicProfile(id);
    }

    // Internal endpoints - called by other microservices via Feign (not exposed through gateway)
    @GetMapping("/internal/users/{id}/summary")
    public UserSummaryDTO getUserSummary(@PathVariable UUID id) {
        return userService.getUserSummary(id);
    }

    @GetMapping("/internal/users/{id}/exists")
    public boolean userExists(@PathVariable UUID id) {
        return userService.existsById(id);
    }
}
