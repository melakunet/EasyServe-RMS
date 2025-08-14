
package com.easyserve.controller;

import com.easyserve.dto.*;
import com.easyserve.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        try {
            UserService.UserProfile user = userService.registerUser(req);
            
            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("user", Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "role", user.getRole()
            ));
            response.put("token", "mock-jwt-token-" + user.getId()); // Mock token for MVP
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            UserService.UserProfile user = userService.authenticateUser(req.getEmail(), req.getPassword());
            
            // Create login response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("user", Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "role", user.getRole(),
                "restaurantId", user.getRestaurantId()
            ));
            response.put("token", "mock-jwt-token-" + user.getId()); // Mock token for MVP
            response.put("expiresIn", 3600); // 1 hour in seconds
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String bearerToken) {
        try {
            // Extract token (remove "Bearer " prefix)
            String token = bearerToken.substring(7);
            
            // Mock token validation - in real implementation, you'd validate JWT
            if (!token.startsWith("mock-jwt-token-")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid token"));
            }
            
            // Extract user ID from mock token
            String userIdStr = token.replace("mock-jwt-token-", "");
            UUID userId = UUID.fromString(userIdStr);
            
            UserService.UserProfile user = userService.getUserById(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", "mock-jwt-token-" + user.getId()); // New token
            response.put("expiresIn", 3600);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Token refresh failed"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // For stateless authentication, client just discards the token
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String bearerToken) {
        try {
            // Extract user ID from token
            String token = bearerToken.substring(7);
            String userIdStr = token.replace("mock-jwt-token-", "");
            UUID userId = UUID.fromString(userIdStr);
            
            UserService.UserProfile user = userService.getUserById(userId);
            
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("email", user.getEmail());
            profile.put("firstName", user.getFirstName());
            profile.put("lastName", user.getLastName());
            profile.put("role", user.getRole());
            profile.put("restaurantId", user.getRestaurantId());
            profile.put("isActive", user.isActive());
            profile.put("lastLogin", user.getLastLogin());
            
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid token"));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> updates,
                                         @RequestHeader("Authorization") String bearerToken) {
        try {
            // Extract user ID from token
            String token = bearerToken.substring(7);
            String userIdStr = token.replace("mock-jwt-token-", "");
            UUID userId = UUID.fromString(userIdStr);
            
            UserService.UserProfile user = userService.updateUserProfile(
                userId, 
                updates.get("firstName"), 
                updates.get("lastName")
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Profile updated successfully");
            response.put("user", Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "role", user.getRole()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Profile update failed"));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwordData,
                                          @RequestHeader("Authorization") String bearerToken) {
        try {
            // Extract user ID from token
            String token = bearerToken.substring(7);
            String userIdStr = token.replace("mock-jwt-token-", "");
            UUID userId = UUID.fromString(userIdStr);
            
            userService.changePassword(
                userId,
                passwordData.get("oldPassword"),
                passwordData.get("newPassword")
            );
            
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid token"));
        }
    }

    // Utility endpoint to get all test users (for development)
    @GetMapping("/test-users")
    public ResponseEntity<?> getTestUsers() {
        return ResponseEntity.ok(Map.of(
            "testUsers", new String[]{
                "admin@restaurant.com (password: password, role: OWNER)",
                "manager@restaurant.com (password: password, role: MANAGER)",
                "staff@restaurant.com (password: password, role: STAFF)"
            },
            "note", "Use these credentials for testing"
        ));
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "service", "auth-controller",
            "timestamp", java.time.LocalDateTime.now()
        ));
    }
}