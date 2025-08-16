
package com.easyserve.security;

import com.easyserve.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.Date;
import java.util.UUID;

import java.nio.charset.StandardCharsets;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKeyBase64;

    @Value("${jwt.access-expiration-ms:900000}") // 15 minutes
    private long accessTokenValidity;

    @Value("${jwt.refresh-expiration-ms:604800000}") // 7 days
    private long refreshTokenValidity;

    private Key secretKey;

@PostConstruct
public void init() {
    // Use the secret directly without Base64 decoding
    this.secretKey = Keys.hmacShaKeyFor(secretKeyBase64.getBytes(StandardCharsets.UTF_8));
}

    public String generateToken(User user, boolean refreshToken) {
        long now = System.currentTimeMillis();
        long validity = now + (refreshToken ? refreshTokenValidity : accessTokenValidity);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId().toString())
                .claim("restaurantId", user.getRestaurant().getId().toString())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(validity))
                .claim("type", refreshToken ? "refresh" : "access")
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateAccessToken(User user) {
        return generateToken(user, false);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, true);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // invalid or expired
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    public String getUserEmail(String token) {
        return getClaims(token).getSubject();
    }

    public UUID getUserIdFromToken(String token) {
        String userId = getClaims(token).get("userId", String.class);
        return UUID.fromString(userId);
    }

    public UUID getRestaurantIdFromToken(String token) {
        String rid = getClaims(token).get("restaurantId", String.class);
        return UUID.fromString(rid);
    }

    public String getUserRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(secretKey)
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }
}
