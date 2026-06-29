package com.compuslink.user.config;

import com.compuslink.user.model.UserRole;
import com.compuslink.user.model.Users;
import com.compuslink.user.repository.UserRepository;
import com.compuslink.user.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
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

    @Value("${app.frontend-url}")
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

        Users user = userRepo.findByEmail(email);
        if (user == null) {
            user = Users.builder()
                    .email(email).fullName(name)
                    .role(UserRole.STUDENT).isVerified(true)
                    .build();
        }

        String accessToken = jwtService.generateToken(email);
        String refreshToken = jwtService.generateRefreshToken(email);
        user.setRefreshTokenHash(hashToken(refreshToken));
        userRepo.save(user);

        String redirect = frontendUrl + "/auth/callback"
                + "?accessToken=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8)
                + "&refreshToken=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);
        response.sendRedirect(redirect);
    }

    private String hashToken(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
