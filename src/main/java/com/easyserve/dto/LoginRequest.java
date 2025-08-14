
package com.easyserve.dto;

import jakarta.validation.constraints.*;
import java.util.UUID;

public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    private UUID restaurantId;
    private Boolean rememberMe = false;
    private String deviceInfo;
    private String ipAddress;

    // Constructors
    public LoginRequest() {}

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
        this.rememberMe = false;
    }

    public LoginRequest(String email, String password, UUID restaurantId) {
        this.email = email;
        this.password = password;
        this.restaurantId = restaurantId;
        this.rememberMe = false;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UUID getRestaurantId() { return restaurantId; }
    public void setRestaurantId(UUID restaurantId) { this.restaurantId = restaurantId; }

    public Boolean getRememberMe() { return rememberMe; }
    public void setRememberMe(Boolean rememberMe) { this.rememberMe = rememberMe; }

    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    // Business logic methods
    public boolean hasRestaurantId() {
        return restaurantId != null;
    }

    public boolean isRememberMeRequested() {
        return rememberMe != null && rememberMe;
    }

    public String getEmailLowerCase() {
        return email != null ? email.toLowerCase().trim() : null;
    }

    public boolean isValidEmail() {
        if (email == null) return false;
        return email.contains("@") && email.contains(".");
    }

    public boolean hasStrongPassword() {
        if (password == null) return false;
        return password.length() >= 8 && 
               password.matches(".*[A-Z].*") && 
               password.matches(".*[a-z].*") && 
               password.matches(".*\\d.*");
    }

    public void sanitizeInput() {
        if (email != null) {
            this.email = email.trim().toLowerCase();
        }
        if (deviceInfo != null) {
            this.deviceInfo = deviceInfo.trim();
        }
    }

    public boolean isValid() {
        return isValidEmail() && 
               password != null && 
               password.length() >= 6;
    }

    public void prepareForAuth() {
        sanitizeInput();
    }

    public void clearPassword() {
        this.password = null;
    }

    // Static factory methods
    public static LoginRequest createSimpleLogin(String email, String password) {
        return new LoginRequest(email, password);
    }

    public static LoginRequest createRestaurantLogin(String email, String password, UUID restaurantId) {
        return new LoginRequest(email, password, restaurantId);
    }

    // Security: Override toString to hide password
    @Override
    public String toString() {
        return "LoginRequest{" +
                "email='" + email + '\'' +
                ", password='[HIDDEN]'" +
                ", restaurantId=" + restaurantId +
                ", rememberMe=" + rememberMe +
                ", deviceInfo='" + deviceInfo + '\'' +
                '}';
    }
}