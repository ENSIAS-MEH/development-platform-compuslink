package com.compuslink.user.controller;

import com.compuslink.user.dto.CvResponse;
import com.compuslink.user.dto.UpdateProfileRequest;
import com.compuslink.user.dto.UserProfileResponse;
import com.compuslink.user.service.CvService;
import com.compuslink.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/me")
public class MeController {

    private final UserService userService;
    private final CvService cvService;

    public MeController(UserService userService, CvService cvService) {
        this.userService = userService;
        this.cvService = cvService;
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

    // --- Profile picture ---
    @PostMapping(value = "/profile/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserProfileResponse uploadPicture(@RequestHeader("X-User-Id") UUID userId,
                                             @RequestParam("file") MultipartFile file) {
        return userService.updateProfilePicture(userId, file);
    }

    @DeleteMapping("/profile/picture")
    public UserProfileResponse deletePicture(@RequestHeader("X-User-Id") UUID userId) {
        return userService.deleteProfilePicture(userId);
    }

    // --- CVs ---
    @GetMapping("/profile/cvs")
    public List<CvResponse> getCvs(@RequestHeader("X-User-Id") UUID userId) {
        return cvService.getMyCvs(userId);
    }

    @PostMapping(value = "/profile/cv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CvResponse uploadCv(@RequestHeader("X-User-Id") UUID userId,
                               @RequestParam("file") MultipartFile file) {
        return cvService.upload(userId, file);
    }

    @DeleteMapping("/profile/cvs/{id}")
    public void deleteCv(@RequestHeader("X-User-Id") UUID userId, @PathVariable UUID id) {
        cvService.delete(userId, id);
    }

    @PatchMapping("/profile/cvs/{id}/default")
    public CvResponse setDefaultCv(@RequestHeader("X-User-Id") UUID userId, @PathVariable UUID id) {
        return cvService.setDefault(userId, id);
    }
}
