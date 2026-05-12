package com.myapp.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private final String secret = "myappsecretkey123456789012345678901234567890";
    private final long expiration = 86400000;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Use reflection to set values since we're not using Spring context
        try {
            var secretField = JwtService.class.getDeclaredField("secret");
            secretField.setAccessible(true);
            secretField.set(jwtService, secret);

            var expirationField = JwtService.class.getDeclaredField("expiration");
            expirationField.setAccessible(true);
            expirationField.setLong(jwtService, expiration);
        } catch (Exception e) {
            fail("Failed to set up JwtService: " + e.getMessage());
        }
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .roles("USER")
                .build();

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .roles("USER")
                .build();

        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .roles("USER")
                .build();

        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_shouldReturnFalseForWrongUser() {
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .roles("USER")
                .build();

        UserDetails otherUser = User.builder()
                .username("otheruser")
                .password("password")
                .roles("USER")
                .build();

        String token = jwtService.generateToken(userDetails);

        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    @Test
    void isTokenValid_shouldReturnFalseForExpiredToken() {
        // Create a service with expired token
        JwtService expiredService = new JwtService();
        try {
            var secretField = JwtService.class.getDeclaredField("secret");
            secretField.setAccessible(true);
            secretField.set(expiredService, secret);

            var expirationField = JwtService.class.getDeclaredField("expiration");
            expirationField.setAccessible(true);
            expirationField.setLong(expiredService, -1000); // Expired
        } catch (Exception e) {
            fail("Failed to set up JwtService: " + e.getMessage());
        }

        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .roles("USER")
                .build();

        String token = expiredService.generateToken(userDetails);

        assertFalse(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void generateToken_withExtraClaims_shouldIncludeClaims() {
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .roles("USER")
                .build();

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");

        String token = jwtService.generateToken(claims, userDetails);

        assertNotNull(token);
        String role = jwtService.extractClaim(token, c -> c.get("role", String.class));
        assertEquals("ADMIN", role);
    }
}
