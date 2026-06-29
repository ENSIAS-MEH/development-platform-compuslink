package com.compuslink.user.service;

import com.compuslink.user.model.Users;
import com.compuslink.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String ACCESS_TOKEN_TYPE = "ACCESS";
    private static final String REFRESH_TOKEN_TYPE = "REFRESH";

    @Value("${jwt.secret}")
    private String secretkey;

    @Value("${jwt.access-token-expiration-ms:1800000}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-ms:604800000}")
    private long refreshTokenExpirationMs;

    private final UserRepository userRepository;

    public JWTService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateToken(String username) {
        Users user = userRepository.findByEmail(username);
        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE);
        if (user != null) {
            claims.put("userId", user.getId().toString());
            claims.put("role", user.getRole().name());
        }
        return generateToken(username, claims, accessTokenExpirationMs);
    }

    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE);
        return generateToken(username, claims, refreshTokenExpirationMs);
    }

    private String generateToken(String username, Map<String, Object> claims, long expirationMs) {
        return Jwts.builder()
                .claims().add(claims).subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .and()
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretkey));
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        return claimResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean validateRefreshToken(String token) {
        return REFRESH_TOKEN_TYPE.equals(extractClaim(token, c -> c.get(TOKEN_TYPE_CLAIM, String.class)))
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
