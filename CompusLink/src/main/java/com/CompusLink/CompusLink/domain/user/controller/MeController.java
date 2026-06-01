package com.CompusLink.CompusLink.domain.user.controller;

import com.CompusLink.CompusLink.domain.marketplace.service.FileStorageService;
import com.CompusLink.CompusLink.domain.user.dto.CurrentUserResponse;
import com.CompusLink.CompusLink.domain.user.dto.UpdateProfileRequest;
import com.CompusLink.CompusLink.domain.user.dto.UserProfileResponse;
import com.CompusLink.CompusLink.domain.user.model.UserPrincipal;
import com.CompusLink.CompusLink.domain.user.model.Users;
import com.CompusLink.CompusLink.domain.user.service.UserService;
import com.CompusLink.CompusLink.domain.user.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/me")
public class MeController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public CurrentUserResponse me(@AuthenticationPrincipal UserPrincipal principal) {
        Users user = principal.getUser();
        return CurrentUserResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    @GetMapping("/profile")
    public UserProfileResponse getProfile() {
        return userService.getMyProfile();
    }

    @PatchMapping("/profile")
    public UserProfileResponse updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateMyProfile(request);
    }

    @PostMapping("/profile/picture")
    public Map<String, String> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        String url = fileStorageService.store(file);
        userService.updateProfilePicUrl(SecurityUtils.getCurrentUserId(), url);
        return Map.of("profilePicUrl", url);
    }

    @PostMapping("/profile/cv")
    public Map<String, String> uploadCv(@RequestParam("file") MultipartFile file) {
        String url = fileStorageService.store(file);
        userService.updateCvUrl(SecurityUtils.getCurrentUserId(), url);
        return Map.of("cvUrl", url);
    }
}
