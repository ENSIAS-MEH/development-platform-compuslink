package com.CompusLink.CompusLink.domain.user.controller;

import com.CompusLink.CompusLink.domain.user.dto.CurrentUserResponse;
import com.CompusLink.CompusLink.domain.user.model.UserPrincipal;
import com.CompusLink.CompusLink.domain.user.model.Users;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
public class MeController {

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
}
