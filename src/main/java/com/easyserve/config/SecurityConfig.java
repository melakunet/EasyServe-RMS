
package com.easyserve.config;

import com.easyserve.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - no authentication required
                .requestMatchers(
                    "/api/public/**",
                    "/api/widget/**",
                    "/api/auth/login",
                    "/api/auth/register",
                    "/api/menu/**",
                    "/h2-console/**"
                ).permitAll()
                
                // Admin endpoints - require ADMIN role
                .requestMatchers("/api/admin/**").hasRole("OWNER")
                
                // Protected endpoints - require authentication
                .requestMatchers(
                    "/api/reservations/**", 
                    "/api/orders/**",
                    "/api/users/**",
                    "/api/dashboard/**"
                ).authenticated()
                
                // Default: deny all other requests
                .anyRequest().denyAll()
            )
            .authenticationProvider(authenticationProvider())
            .headers(headers -> headers
                .frameOptions().sameOrigin()
                .httpStrictTransportSecurity().disable()
            );

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        
        // Allow all origins for development (restrict in production)
        config.setAllowedOriginPatterns(List.of("*"));
        
        // Allow all HTTP methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Allow all headers
        config.setAllowedHeaders(List.of("*"));
        
        // Expose headers for frontend
        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    // Bean for HTTP Basic Authentication (for testing)
    @Bean
    public org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint basicAuthenticationEntryPoint() {
        org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint entryPoint = 
            new org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint();
        entryPoint.setRealmName("EasyServe");
        return entryPoint;
    }
}