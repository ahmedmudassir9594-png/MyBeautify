package com.cosmetics.ecommerce;

import com.cosmetics.ecommerce.dto.AuthDtos.LoginRequest;
import com.cosmetics.ecommerce.dto.AuthDtos.RegisterRequest;
import com.cosmetics.ecommerce.dto.ProfileDtos.ResetPasswordRequest;
import com.cosmetics.ecommerce.entity.PasswordResetToken;
import com.cosmetics.ecommerce.entity.User;
import com.cosmetics.ecommerce.enums.Role;
import com.cosmetics.ecommerce.repository.PasswordResetTokenRepository;
import com.cosmetics.ecommerce.repository.UserRepository;
import com.cosmetics.ecommerce.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository resetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        resetTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Registration assigns CUSTOMER role and hashes password with BCrypt")
    void registrationAssignsCustomerRoleAndHashesPassword() throws Exception {
        RegisterRequest request = new RegisterRequest("Sarah", "Connor", "sarah@example.com", "SecureP@ss123", "+1234567890");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isString())
                .andExpect(jsonPath("$.data.refreshToken").isString())
                .andExpect(jsonPath("$.data.user.role").value("CUSTOMER"));

        User savedUser = userRepository.findByEmailIgnoreCase("sarah@example.com").orElseThrow();
        assertThat(savedUser.getRole()).isEqualTo(Role.CUSTOMER);
        assertThat(savedUser.getPassword()).startsWith("$2a$");
        assertThat(passwordEncoder.matches("SecureP@ss123", savedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("Login with incorrect password returns 401 Unauthorized")
    void loginWithIncorrectPasswordReturns401() throws Exception {
        User user = new User("John", "Doe", "john@example.com", passwordEncoder.encode("CorrectPassword1"), Role.CUSTOMER);
        userRepository.save(user);

        LoginRequest request = new LoginRequest("john@example.com", "WrongPassword999");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password."));
    }

    @Test
    @DisplayName("Login for deactivated user returns 401 Unauthorized")
    void loginForDeactivatedUserReturns401() throws Exception {
        User user = new User("Disabled", "User", "disabled@example.com", passwordEncoder.encode("Password123"), Role.CUSTOMER);
        user.setEnabled(false);
        userRepository.save(user);

        LoginRequest request = new LoginRequest("disabled@example.com", "Password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("This account has been deactivated."));
    }

    @Test
    @DisplayName("Protected endpoint accepts valid access token")
    void protectedEndpointAcceptsValidAccessToken() throws Exception {
        User user = new User("Alice", "Smith", "alice@example.com", passwordEncoder.encode("Password123"), Role.CUSTOMER);
        userRepository.save(user);

        String accessToken = jwtService.accessToken(user);

        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("alice@example.com"));
    }

    @Test
    @DisplayName("Protected endpoint rejects refresh token when used as access token (Token Confusion Prevention)")
    void protectedEndpointRejectsRefreshTokenAsAccessToken() throws Exception {
        User user = new User("Bob", "Brown", "bob@example.com", passwordEncoder.encode("Password123"), Role.CUSTOMER);
        userRepository.save(user);

        String refreshToken = jwtService.refreshToken(user);

        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Protected endpoint rejects deactivated user even with valid access token")
    void protectedEndpointRejectsDeactivatedUserWithValidAccessToken() throws Exception {
        User user = new User("Charlie", "Green", "charlie@example.com", passwordEncoder.encode("Password123"), Role.CUSTOMER);
        userRepository.save(user);

        String accessToken = jwtService.accessToken(user);

        // Deactivate user after token creation
        user.setEnabled(false);
        userRepository.save(user);

        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Password reset flow successfully updates password and invalidates token")
    void passwordResetUpdatesPasswordAndInvalidatesToken() throws Exception {
        User user = new User("Reset", "User", "reset@example.com", passwordEncoder.encode("OldPassword123"), Role.CUSTOMER);
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        resetTokenRepository.save(new PasswordResetToken(token, user, Instant.now().plus(30, ChronoUnit.MINUTES)));

        ResetPasswordRequest request = new ResetPasswordRequest(token, "BrandNewPassword456");

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        User updatedUser = userRepository.findByEmailIgnoreCase("reset@example.com").orElseThrow();
        assertThat(passwordEncoder.matches("BrandNewPassword456", updatedUser.getPassword())).isTrue();

        // Ensure token was deleted so it cannot be re-used
        assertThat(resetTokenRepository.findByToken(token)).isEmpty();
    }
}
