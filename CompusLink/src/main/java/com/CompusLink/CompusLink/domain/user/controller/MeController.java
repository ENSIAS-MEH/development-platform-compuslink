package com.CompusLink.CompusLink.domain.user.controller;

import com.CompusLink.CompusLink.domain.marketplace.service.FileStorageService;
import com.CompusLink.CompusLink.domain.user.dto.CurrentUserResponse;
import com.CompusLink.CompusLink.domain.user.dto.UpdateProfileRequest;
import com.CompusLink.CompusLink.domain.user.dto.UserProfileResponse;
import com.CompusLink.CompusLink.domain.user.model.UserCv;
import com.CompusLink.CompusLink.domain.user.model.UserPrincipal;
import com.CompusLink.CompusLink.domain.user.model.Users;
import com.CompusLink.CompusLink.domain.user.repository.UserCvRepository;
import com.CompusLink.CompusLink.domain.user.service.UserService;
import com.CompusLink.CompusLink.domain.user.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/me")
public class MeController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UserCvRepository userCvRepository;

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

    @DeleteMapping("/profile/picture")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfilePicture() {
        userService.updateProfilePicUrl(SecurityUtils.getCurrentUserId(), null);
    }

    // --- Multiple CV endpoints ---

    @PostMapping("/profile/cv")
    public UserCv uploadCv(@RequestParam("file") MultipartFile file) {
        UUID userId = SecurityUtils.getCurrentUserId();
        String url = fileStorageService.store(file);
        String originalName = file.getOriginalFilename();
        UserCv cv = UserCv.builder()
                .userId(userId)
                .fileUrl(url)
                .originalName(originalName)
                .build();
        return userCvRepository.save(cv);
    }

    @GetMapping("/profile/cvs")
    public List<UserCv> listCvs() {
        return userCvRepository.findByUserIdOrderByUploadedAtDesc(SecurityUtils.getCurrentUserId());
    }

    @DeleteMapping("/profile/cvs/{cvId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCv(@PathVariable UUID cvId) {
        UserCv cv = userCvRepository.findById(cvId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!cv.getUserId().equals(SecurityUtils.getCurrentUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        fileStorageService.delete(cv.getFileUrl());
        userCvRepository.delete(cv);
    }

    @PatchMapping("/profile/cvs/{cvId}/default")
    public UserCv setDefaultCv(@PathVariable UUID cvId) {
        UUID userId = SecurityUtils.getCurrentUserId();
        UserCv cv = userCvRepository.findById(cvId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!cv.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        // Unset all defaults for this user
        List<UserCv> allCvs = userCvRepository.findByUserIdOrderByUploadedAtDesc(userId);
        allCvs.forEach(c -> c.setDefault(false));
        userCvRepository.saveAll(allCvs);
        // Set the selected one
        cv.setDefault(true);
        return userCvRepository.save(cv);
    }
}
