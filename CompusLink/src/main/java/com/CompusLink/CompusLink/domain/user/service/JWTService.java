package com.CompusLink.CompusLink.domain.user.service;

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

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE);
        return generateToken(username, claims, accessTokenExpirationMs);
    }

    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE);
        return generateToken(username, claims, refreshTokenExpirationMs);
    }

    private String generateToken(String username, Map<String, Object> claims, long expirationMs) {
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .and()
                .signWith(getKey())
                .compact();

    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretkey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean validateRefreshToken(String token) {
        return REFRESH_TOKEN_TYPE.equals(extractTokenType(token)) && !isTokenExpired(token);
    }

    private String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get(TOKEN_TYPE_CLAIM, String.class));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
