package com.cosmetics.ecommerce.config;

import com.cosmetics.ecommerce.security.JwtAuthenticationFilter;
import com.cosmetics.ecommerce.repository.UserRepository;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final String allowedOrigins;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          @Value("${app.cors.allowed-origins:${CORS_ALLOWED_ORIGINS:http://localhost:3000,http://localhost:5173}}") String allowedOrigins) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.allowedOrigins = allowedOrigins;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(org.springframework.web.cors.CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/healthz",
                                "/auth/register",
                                "/auth/login",
                                "auth/google",
                                "/auth/refresh-token",
                                "/auth/forgot-password",
                                "/auth/reset-password",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/referrals/validate"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/**", "/categories/**", "/categoriesASC/**", "/brands/**").permitAll()
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "STAFF")
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            try (PrintWriter writer = response.getWriter()) {
                                writer.write("{\"success\":false,\"message\":\"Full authentication is required to access this resource.\",\"status\":401,\"timestamp\":\"" + java.time.Instant.now() + "\"}");
                            }
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            try (PrintWriter writer = response.getWriter()) {
                                writer.write("{\"success\":false,\"message\":\"You do not have permission to perform this action.\",\"status\":403,\"timestamp\":\"" + java.time.Instant.now() + "\"}");
                            }
                        }))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    org.springframework.security.core.userdetails.UserDetailsService userDetailsService(UserRepository users) {
        return username -> users.findByEmailIgnoreCase(username)
                .filter(com.cosmetics.ecommerce.entity.User::isEnabled)
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found or disabled."));
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.stream(allowedOrigins.split(",")).map(String::trim).toList());
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
        config.setExposedHeaders(Arrays.asList("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}