
package com.easyserve.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.util.UUID;

public class RegisterRequest {

    // User Information
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(OWNER|MANAGER|STAFF)$", message = "Role must be OWNER, MANAGER, or STAFF")
    private String role;

    // Restaurant Information (required for OWNER role)
    private String restaurantName;

    @Email(message = "Please provide a valid restaurant email")
    private String restaurantEmail;

    @Pattern(regexp = "^[+]?[1-9]\\d{1,14}$", message = "Please provide a valid phone number")
    private String restaurantPhone;

    @Size(max = 200, message = "Restaurant address cannot exceed 200 characters")
    private String restaurantAddress;

    @Pattern(regexp = "^(BASIC|PRO|PREMIUM|ENTERPRISE)$", message = "Subscription plan must be BASIC, PRO, PREMIUM, or ENTERPRISE")
    private String subscriptionPlan;

    // Optional: Existing restaurant ID (for MANAGER/STAFF joining existing restaurant)
    private UUID existingRestaurantId;

    // Optional: Invitation code for staff
    private String invitationCode;

    // Constructors
    public RegisterRequest() {}

    public RegisterRequest(String firstName, String lastName, String email, String password, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public String getRestaurantEmail() { return restaurantEmail; }
    public void setRestaurantEmail(String restaurantEmail) { this.restaurantEmail = restaurantEmail; }

    public String getRestaurantPhone() { return restaurantPhone; }
    public void setRestaurantPhone(String restaurantPhone) { this.restaurantPhone = restaurantPhone; }

    public String getRestaurantAddress() { return restaurantAddress; }
    public void setRestaurantAddress(String restaurantAddress) { this.restaurantAddress = restaurantAddress; }

    public String getSubscriptionPlan() { return subscriptionPlan; }
    public void setSubscriptionPlan(String subscriptionPlan) { this.subscriptionPlan = subscriptionPlan; }

    public UUID getExistingRestaurantId() { return existingRestaurantId; }
    public void setExistingRestaurantId(UUID existingRestaurantId) { this.existingRestaurantId = existingRestaurantId; }

    public String getInvitationCode() { return invitationCode; }
    public void setInvitationCode(String invitationCode) { this.invitationCode = invitationCode; }

    // Business Logic Methods
    public boolean isOwnerRegistration() {
        return "OWNER".equals(role);
    }

    public boolean isStaffRegistration() {
        return "MANAGER".equals(role) || "STAFF".equals(role);
    }

    public boolean passwordsMatch() {
        return password != null && password.equals(confirmPassword);
    }

    public boolean hasStrongPassword() {
        if (password == null) return false;
        return password.length() >= 8 && 
               password.matches(".*[A-Z].*") && // Has uppercase
               password.matches(".*[a-z].*") && // Has lowercase
               password.matches(".*\\d.*") &&   // Has digit
               password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*"); // Has special char
    }

    public boolean requiresRestaurantInfo() {
        return isOwnerRegistration();
    }

    public boolean requiresExistingRestaurant() {
        return isStaffRegistration();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getEmailLowerCase() {
        return email != null ? email.toLowerCase().trim() : null;
    }

    // Validation Methods
    public boolean isValidForOwnerRegistration() {
        return isOwnerRegistration() && 
               restaurantName != null && !restaurantName.trim().isEmpty() &&
               restaurantEmail != null && !restaurantEmail.trim().isEmpty() &&
               subscriptionPlan != null && !subscriptionPlan.trim().isEmpty();
    }

    public boolean isValidForStaffRegistration() {
        return isStaffRegistration() && 
               (existingRestaurantId != null || 
                (invitationCode != null && !invitationCode.trim().isEmpty()));
    }

    public boolean isValid() {
        return passwordsMatch() && 
               hasStrongPassword() &&
               (isValidForOwnerRegistration() || isValidForStaffRegistration() || 
                (!isOwnerRegistration() && !isStaffRegistration()));
    }

    // Input Sanitization
    public void sanitizeInput() {
        if (email != null) this.email = email.trim().toLowerCase();
        if (firstName != null) this.firstName = firstName.trim();
        if (lastName != null) this.lastName = lastName.trim();
        if (restaurantName != null) this.restaurantName = restaurantName.trim();
        if (restaurantEmail != null) this.restaurantEmail = restaurantEmail.trim().toLowerCase();
        if (restaurantPhone != null) this.restaurantPhone = restaurantPhone.trim();
        if (restaurantAddress != null) this.restaurantAddress = restaurantAddress.trim();
        if (invitationCode != null) this.invitationCode = invitationCode.trim();
    }

    // Factory Methods
    public static RegisterRequest createOwnerRegistration(String firstName, String lastName, 
                                                        String email, String password, 
                                                        String restaurantName, String restaurantEmail,
                                                        String subscriptionPlan) {
        RegisterRequest request = new RegisterRequest(firstName, lastName, email, password, "OWNER");
        request.setRestaurantName(restaurantName);
        request.setRestaurantEmail(restaurantEmail);
        request.setSubscriptionPlan(subscriptionPlan);
        request.setConfirmPassword(password);
        return request;
    }

    public static RegisterRequest createStaffRegistration(String firstName, String lastName,
                                                        String email, String password,
                                                        String role, UUID restaurantId) {
        RegisterRequest request = new RegisterRequest(firstName, lastName, email, password, role);
        request.setExistingRestaurantId(restaurantId);
        request.setConfirmPassword(password);
        return request;
    }

    // Security: Clear sensitive data
    public void clearPasswords() {
        this.password = null;
        this.confirmPassword = null;
    }

    // Security: Override toString to hide passwords
    @Override
    public String toString() {
        return "RegisterRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='[HIDDEN]'" +
                ", confirmPassword='[HIDDEN]'" +
                ", role='" + role + '\'' +
                ", restaurantName='" + restaurantName + '\'' +
                ", restaurantEmail='" + restaurantEmail + '\'' +
                ", restaurantPhone='" + restaurantPhone + '\'' +
                ", subscriptionPlan='" + subscriptionPlan + '\'' +
                ", existingRestaurantId=" + existingRestaurantId +
                '}';
    }
}
