package com.CompusLink.CompusLink.config;

import com.CompusLink.CompusLink.domain.user.model.UserRole;
import com.CompusLink.CompusLink.domain.user.model.Users;
import com.CompusLink.CompusLink.domain.user.repository.UserRepository;
import com.CompusLink.CompusLink.domain.user.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepo;
    private final JWTService jwtService;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    public OAuth2SuccessHandler(UserRepository userRepo, JWTService jwtService) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Find or create user
        Users user = userRepo.findByEmail(email);
        if (user == null) {
            user = Users.builder()
                    .email(email)
                    .fullName(name)
                    .role(UserRole.STUDENT)
                    .isVerified(true)
                    .build();
        }

        // Generate tokens
        String accessToken = jwtService.generateToken(email);
        String refreshToken = jwtService.generateRefreshToken(email);
        user.setRefreshTokenHash(hashToken(refreshToken));
        userRepo.save(user);

        // Redirect to frontend with tokens
        String redirect = frontendUrl + "/auth/callback"
                + "?accessToken=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8)
                + "&refreshToken=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);
        response.sendRedirect(redirect);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
