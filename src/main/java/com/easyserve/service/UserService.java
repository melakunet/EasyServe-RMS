
package com.easyserve.service;

import com.easyserve.dto.LoginRequest;
import com.easyserve.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;

@Service
public class UserService {

    @Autowired
    private NotificationService notificationService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Mock storage for users (in-memory for MVP)
    private final Map<UUID, UserProfile> users = new ConcurrentHashMap<>();
    private final Map<String, UUID> emailToUserId = new ConcurrentHashMap<>();

    // User profile class for mock storage
    public static class UserProfile {
        private UUID id;
        private String email;
        private String password; // encrypted
        private String firstName;
        private String lastName;
        private String role; // OWNER, MANAGER, STAFF
        private UUID restaurantId;
        private boolean isActive;
        private LocalDateTime lastLogin;
        private LocalDateTime createdAt;

        // Constructors
        public UserProfile() {}

        public UserProfile(UUID id, String email, String password, String firstName, 
                          String lastName, String role, UUID restaurantId) {
            this.id = id;
            this.email = email;
            this.password = password;
            this.firstName = firstName;
            this.lastName = lastName;
            this.role = role;
            this.restaurantId = restaurantId;
            this.isActive = true;
            this.createdAt = LocalDateTime.now();
        }

        // Getters and setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public UUID getRestaurantId() { return restaurantId; }
        public void setRestaurantId(UUID restaurantId) { this.restaurantId = restaurantId; }
        
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
        
        public LocalDateTime getLastLogin() { return lastLogin; }
        public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public String getFullName() {
            return firstName + " " + lastName;
        }
    }

    public UserService() {
        // Initialize with some test users
        createTestUsers();
    }

    private void createTestUsers() {
        UUID restaurantId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        
        // Owner user
        UserProfile owner = new UserProfile(
            UUID.randomUUID(),
            "admin@restaurant.com",
            passwordEncoder.encode("password"),
            "John",
            "Owner",
            "OWNER",
            restaurantId
        );
        users.put(owner.getId(), owner);
        emailToUserId.put(owner.getEmail(), owner.getId());

        // Manager user
        UserProfile manager = new UserProfile(
            UUID.randomUUID(),
            "manager@restaurant.com",
            passwordEncoder.encode("password"),
            "Jane",
            "Manager",
            "MANAGER",
            restaurantId
        );
        users.put(manager.getId(), manager);
        emailToUserId.put(manager.getEmail(), manager.getId());

        // Staff user
        UserProfile staff = new UserProfile(
            UUID.randomUUID(),
            "staff@restaurant.com",
            passwordEncoder.encode("password"),
            "Bob",
            "Staff",
            "STAFF",
            restaurantId
        );
        users.put(staff.getId(), staff);
        emailToUserId.put(staff.getEmail(), staff.getId());
    }

    public UserProfile registerUser(RegisterRequest request) {
        // Check if email already exists
        if (emailToUserId.containsKey(request.getEmail())) {
            throw new IllegalArgumentException("User already exists with this email");
        }

        // Create new user
        UUID userId = UUID.randomUUID();
        UserProfile user = new UserProfile(
            userId,
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            request.getFirstName(),
            request.getLastName(),
            request.getRole(),
            request.getExistingRestaurantId() != null ? 
                request.getExistingRestaurantId() : UUID.randomUUID()
        );

        // Save user
        users.put(userId, user);
        emailToUserId.put(user.getEmail(), userId);

        // Send welcome email
        notificationService.sendWelcomeEmail(user.getEmail(), user.getFirstName());

        return user;
    }

    public UserProfile authenticateUser(String email, String rawPassword) {
        UUID userId = emailToUserId.get(email);
        if (userId == null) {
            throw new IllegalArgumentException("Invalid email or inactive user");
        }

        UserProfile user = users.get(userId);
        if (user == null || !user.isActive()) {
            throw new IllegalArgumentException("Invalid email or inactive user");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Update last login time
        user.setLastLogin(LocalDateTime.now());
        return user;
    }

    public UserProfile updateUserProfile(UUID userId, String firstName, String lastName) {
        UserProfile user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }

    public void deactivateUser(UUID userId) {
        UserProfile user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        user.setActive(false);
    }

    public List<UserProfile> getUsersByRestaurant(UUID restaurantId) {
        return users.values().stream()
                .filter(user -> user.getRestaurantId().equals(restaurantId))
                .filter(UserProfile::isActive)
                .sorted((u1, u2) -> u1.getRole().compareTo(u2.getRole()))
                .toList();
    }

    public void changeUserRole(UUID userId, String newRole) {
        UserProfile user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        user.setRole(newRole);
    }

    public UserProfile getUserById(UUID userId) {
        UserProfile user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return user;
    }

    public UserProfile getUserByEmail(String email) {
        UUID userId = emailToUserId.get(email);
        if (userId == null) {
            throw new IllegalArgumentException("User not found");
        }
        return users.get(userId);
    }

    public boolean emailExists(String email) {
        return emailToUserId.containsKey(email);
    }

    public void changePassword(UUID userId, String oldPassword, String newPassword) {
        UserProfile user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid current password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
    }

    public void resetPassword(String email, String newPassword) {
        UUID userId = emailToUserId.get(email);
        if (userId == null) {
            throw new IllegalArgumentException("User not found");
        }

        UserProfile user = users.get(userId);
        user.setPassword(passwordEncoder.encode(newPassword));

        // Send password reset notification
        notificationService.sendPasswordResetEmail(email, "reset-token-" + UUID.randomUUID());
    }

    public List<UserProfile> getActiveUsers() {
        return users.values().stream()
                .filter(UserProfile::isActive)
                .sorted((u1, u2) -> u1.getEmail().compareTo(u2.getEmail()))
                .toList();
    }

    public List<UserProfile> getUsersByRole(String role) {
        return users.values().stream()
                .filter(user -> role.equals(user.getRole()))
                .filter(UserProfile::isActive)
                .toList();
    }

    // Analytics methods
    public int getTotalUsers() {
        return users.size();
    }

    public int getActiveUsersCount() {
        return (int) users.values().stream()
                .filter(UserProfile::isActive)
                .count();
    }

    public int getUsersByRoleCount(String role) {
        return (int) users.values().stream()
                .filter(user -> role.equals(user.getRole()))
                .filter(UserProfile::isActive)
                .count();
    }

    // Business logic methods
    public boolean canUserAccessRestaurant(UUID userId, UUID restaurantId) {
        UserProfile user = users.get(userId);
        return user != null && user.getRestaurantId().equals(restaurantId) && user.isActive();
    }

    public boolean isUserOwner(UUID userId) {
        UserProfile user = users.get(userId);
        return user != null && "OWNER".equals(user.getRole());
    }

    public boolean isUserManager(UUID userId) {
        UserProfile user = users.get(userId);
        return user != null && ("OWNER".equals(user.getRole()) || "MANAGER".equals(user.getRole()));
    }

    public List<UserProfile> getRestaurantStaff(UUID restaurantId) {
        return users.values().stream()
                .filter(user -> user.getRestaurantId().equals(restaurantId))
                .filter(UserProfile::isActive)
                .filter(user -> "STAFF".equals(user.getRole()))
                .toList();
    }
}