
package com.easyserve.security;

import com.easyserve.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // For now, we'll create a simple mock user until we have the full User entity
        // This allows the class to compile without errors
        
        // TODO: Replace this mock implementation when User entity and repository are created
        if ("admin@restaurant.com".equals(email)) {
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_OWNER"));
            return new org.springframework.security.core.userdetails.User(
                    email,
                    "$2a$10$dXJ3SW6G7P4LaVlCEkO3v.3pR/a/v5gqe4JB8Lk5b3B8KdMl1JH8m", // bcrypt encoded "password"
                    true, // enabled
                    true, // account non expired
                    true, // credentials non expired
                    true, // account non locked
                    authorities
            );
        } else if ("manager@restaurant.com".equals(email)) {
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_MANAGER"));
            return new org.springframework.security.core.userdetails.User(
                    email,
                    "$2a$10$dXJ3SW6G7P4LaVlCEkO3v.3pR/a/v5gqe4JB8Lk5b3B8KdMl1JH8m", // bcrypt encoded "password"
                    true, true, true, true, authorities
            );
        } else if ("staff@restaurant.com".equals(email)) {
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_STAFF"));
            return new org.springframework.security.core.userdetails.User(
                    email,
                    "$2a$10$dXJ3SW6G7P4LaVlCEkO3v.3pR/a/v5gqe4JB8Lk5b3B8KdMl1JH8m", // bcrypt encoded "password"
                    true, true, true, true, authorities
            );
        }

        throw new UsernameNotFoundException("User not found: " + email);
    }

    // Helper method to create mock user details
    private UserDetails createUserDetails(String email, String role) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
        return new org.springframework.security.core.userdetails.User(
                email,
                "$2a$10$dXJ3SW6G7P4LaVlCEkO3v.3pR/a/v5gqe4JB8Lk5b3B8KdMl1JH8m", // bcrypt encoded "password"
                true, // enabled
                true, // account non expired
                true, // credentials non expired
                true, // account non locked
                authorities
        );
    }
}