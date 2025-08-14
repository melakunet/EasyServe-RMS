

package com.easyserve.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    private String tokenType = "Bearer";

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("user")
    private UserInfo user;

    @JsonProperty("restaurant")
    private RestaurantInfo restaurant;

    @JsonProperty("login_time")
    private LocalDateTime loginTime;

    // Nested class for user information
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        
        @JsonProperty("id")
        private UUID id;

        @JsonProperty("email")
        private String email;

        @JsonProperty("first_name")
        private String firstName;

        @JsonProperty("last_name")
        private String lastName;

        @JsonProperty("role")
        private String role;

        @JsonProperty("is_active")
        private Boolean isActive;

        // Helper method for full name
        public String getFullName() {
            return firstName + " " + lastName;
        }

        // Check if user is owner
        public boolean isOwner() {
            return "OWNER".equals(role);
        }

        // Check if user is manager
        public boolean isManager() {
            return "MANAGER".equals(role);
        }

        // Check if user is staff
        public boolean isStaff() {
            return "STAFF".equals(role);
        }
    }

    // Nested class for restaurant information
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RestaurantInfo {
        
        @JsonProperty("id")
        private UUID id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("email")
        private String email;

        @JsonProperty("phone")
        private String phone;

        @JsonProperty("subscription_plan")
        private String subscriptionPlan;

        @JsonProperty("is_active")
        private Boolean isActive;

        // Check subscription level
        public boolean isPremiumPlan() {
            return "PREMIUM".equals(subscriptionPlan) || "ENTERPRISE".equals(subscriptionPlan);
        }

        public boolean isBasicPlan() {
            return "BASIC".equals(subscriptionPlan);
        }
    }

    // Static factory method to create successful login response
    public static LoginResponse createSuccessResponse(String accessToken, String refreshToken, 
                                                    Long expiresIn, UserInfo user, RestaurantInfo restaurant) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .user(user)
                .restaurant(restaurant)
                .loginTime(LocalDateTime.now())
                .build();
    }

    // Helper methods
    public boolean isTokenValid() {
        return accessToken != null && !accessToken.trim().isEmpty();
    }

    public boolean hasRefreshToken() {
        return refreshToken != null && !refreshToken.trim().isEmpty();
    }

    public String getAuthorizationHeader() {
        return tokenType + " " + accessToken;
    }

    // Check if token is about to expire (within 5 minutes)
    public boolean isTokenExpiringSoon() {
        if (expiresIn == null) return false;
        return expiresIn <= 300; // 5 minutes in seconds
    }

    // Get user role for authorization
    public String getUserRole() {
        return user != null ? user.getRole() : null;
    }

    // Get restaurant ID for multi-tenant operations
    public UUID getRestaurantId() {
        return restaurant != null ? restaurant.getId() : null;
    }
}
