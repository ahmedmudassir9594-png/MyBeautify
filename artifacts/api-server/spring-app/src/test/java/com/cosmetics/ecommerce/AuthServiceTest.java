package com.cosmetics.ecommerce;

import com.cosmetics.ecommerce.dto.AuthDtos;
import com.cosmetics.ecommerce.entity.User;
import com.cosmetics.ecommerce.enums.Role;
import com.cosmetics.ecommerce.repository.UserRepository;
import com.cosmetics.ecommerce.security.JwtService;
import com.cosmetics.ecommerce.service.AuthService;
import com.cosmetics.ecommerce.email.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AuthServiceTest {
    @Mock UserRepository users;
    @Mock PasswordEncoder encoder;
    @Mock JwtService jwt;
    @Mock EmailService email;
    @InjectMocks AuthService service;

    @Test
    void registerHashesPasswordAndReturnsTokens() {
        when(users.existsByEmailIgnoreCase("maya@example.com")).thenReturn(false);
        when(encoder.encode("password123")).thenReturn("hashed");
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwt.accessToken(any())).thenReturn("access");
        when(jwt.refreshToken(any())).thenReturn("refresh");

        AuthDtos.AuthResponse response = service.register(new AuthDtos.RegisterRequest(
                "Maya", "Perera", "maya@example.com", "password123", "+9411"));

        assertThat(response.accessToken()).isEqualTo("access");
        assertThat(response.refreshToken()).isEqualTo("refresh");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(users).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("hashed");
        assertThat(captor.getValue().getRole()).isEqualTo(Role.CUSTOMER);
    }

    @Test
    void loginRejectsWrongPasswordWithoutIssuingTokens() {
        User user = new User("Maya", "Perera", "maya@example.com", "hashed", Role.CUSTOMER);
        when(users.findByEmailIgnoreCase("maya@example.com")).thenReturn(Optional.of(user));
        when(encoder.matches("wrong", "hashed")).thenReturn(false);

        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                service.login(new AuthDtos.LoginRequest("maya@example.com", "wrong")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid email or password.");
        verifyNoInteractions(jwt);
    }
}