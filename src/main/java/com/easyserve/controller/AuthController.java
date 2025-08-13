
package com.easyserve.controller;

import com.easyserve.dto.*;
import com.easyserve.model.User;
import com.easyserve.service.UserService;
import com.easyserve.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        User user = userService.registerUser(req.toUser(), req.getRestaurantId());
        String token = jwtTokenProvider.createToken(user.getId(), user.getRole().name());
        return ResponseEntity.ok(new AuthResponse(token, user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        User user = userService.authenticateUser(req.getEmail(), req.getPassword());
        String token = jwtTokenProvider.createToken(user.getId(), user.getRole().name());
        return ResponseEntity.ok(new AuthResponse(token, user));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestHeader("Authorization") String bearerToken) {
        String token = bearerToken.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = jwtTokenProvider.getUserIdFromToken(token);
        User user = userService.findById(userId);
        String newToken = jwtTokenProvider.createToken(user.getId(), user.getRole().name());
        return ResponseEntity.ok(new AuthResponse(newToken, user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Stateless—token expires automatically
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@RequestHeader("Authorization") String bearerToken) {
        UUID userId = jwtTokenProvider.getUserIdFromToken(bearerToken.substring(7));
        User user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@Valid @RequestBody ProfileUpdateRequest req,
                                              @RequestHeader("Authorization") String bearerToken) {
        UUID userId = jwtTokenProvider.getUserIdFromToken(bearerToken.substring(7));
        User updated = userService.updateUserProfile(userId, req.toUser());
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody PasswordChangeRequest req,
                                               @RequestHeader("Authorization") String bearerToken) {
        UUID userId = jwtTokenProvider.getUserIdFromToken(bearerToken.substring(7));
        userService.changePassword(userId, req.getOldPassword(), req.getNewPassword());
        return ResponseEntity.noContent().build();
    }
}
