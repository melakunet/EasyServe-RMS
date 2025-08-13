
package com.easyserve.service;

import com.easyserve.model.Restaurant;
import com.easyserve.model.User;
import com.easyserve.model.User.Role;
import com.easyserve.repository.RestaurantRepository;
import com.easyserve.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(User user, UUID restaurantId) {
        if (userRepository.existsByEmailAndRestaurantId(user.getEmail(), restaurantId)) {
            throw new IllegalArgumentException("User already exists for this restaurant");
        }

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        user.setRestaurant(restaurant);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.STAFF);
        user.setIsActive(true);

        return userRepository.save(user);
    }

    public User authenticateUser(String email, String rawPassword) {
        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or inactive user"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Example: update last login time
        user.setLastLogin(java.time.LocalDateTime.now());
        return user;
    }

    public User updateUserProfile(UUID userId, User userDetails) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        existingUser.setFirstName(userDetails.getFirstName());
        existingUser.setLastName(userDetails.getLastName());

        return userRepository.save(existingUser);
    }

    public void deactivateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setIsActive(false);
        userRepository.save(user);
    }

    public List<User> getUsersByRestaurant(UUID restaurantId) {
        return userRepository.findByRestaurantIdAndIsActiveTrue(restaurantId);
    }

    public void changeUserRole(UUID userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setRole(newRole);
        userRepository.save(user);
    }
}
