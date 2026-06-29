package com.CompusLink.CompusLink.domain.user.controller;

import com.CompusLink.CompusLink.domain.user.dto.AuthResponse;
import com.CompusLink.CompusLink.domain.user.dto.LoginRequest;
import com.CompusLink.CompusLink.domain.user.dto.RefreshTokenRequest;
import com.CompusLink.CompusLink.domain.user.dto.RegisterRequest;
import com.CompusLink.CompusLink.domain.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    UserService service;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return service.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return service.verify(request);
    }

    @PostMapping("/refresh-token")
    public AuthResponse refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return service.refreshToken(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        service.logout(request);
        return ResponseEntity.noContent().build();
    }
}
