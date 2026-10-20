package com.cosmetics.ecommerce.service;

import com.cosmetics.ecommerce.dto.*;
import com.cosmetics.ecommerce.entity.User;
import com.cosmetics.ecommerce.enums.Role;
import com.cosmetics.ecommerce.exception.*;
import com.cosmetics.ecommerce.repository.UserRepository;
import com.cosmetics.ecommerce.security.JwtService;
import com.cosmetics.ecommerce.repository.PasswordResetTokenRepository;
import com.cosmetics.ecommerce.dto.ProfileDtos.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import com.cosmetics.ecommerce.email.EmailService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final GoogleTokenService googleTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PasswordResetTokenRepository resetTokens;
    private final EmailService emailService;
    private final ReferralService referralService;

    public AuthService(GoogleTokenService googleTokenService, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
                       PasswordResetTokenRepository resetTokens, EmailService emailService, ReferralService referralService) {
        this.googleTokenService = googleTokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.resetTokens = resetTokens;
        this.emailService = emailService;
        this.referralService = referralService;
    }

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) throw new DuplicateResourceException("Email is already registered.");
        String firstName = request.effectiveFirstName();
        String lastName = request.effectiveLastName();
        String fullName = request.effectiveFullName();
        User user = new User(firstName, lastName, email, passwordEncoder.encode(request.password()), Role.CUSTOMER);
        user.setFullName(fullName);
        user.setPhoneNumber(request.phoneNumber());
        userRepository.save(user);

        if (request.referralCode() != null && !request.referralCode().isBlank()) {
            referralService.linkReferralOnRegister(user, request.referralCode());
        }

        emailService.sendWelcome(user.getEmail(), user.getFirstName());
        return tokens(user);
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password."));
        if (!user.isEnabled()) {
            throw new UnauthorizedException("This account has been deactivated.");
        }
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password.");
        }
        return tokens(user);
    }

    @Transactional
    public AuthDtos.AuthResponse googleLogin(String credential) {
        if (credential == null || credential.isBlank()) {
            throw new UnauthorizedException("Google credential is required.");
        }

        final GoogleIdToken.Payload payload;

        try {
            payload = googleTokenService.verify(credential);
        } catch (GeneralSecurityException | IOException | IllegalArgumentException ex) {
            throw new UnauthorizedException("Invalid Google credential.");
        }

        String googleId = payload.getSubject();
        String email = payload.getEmail().trim().toLowerCase();

        // Check whether this Google account is already linked.
        Optional<User> linkedUser = userRepository.findByGoogleId(googleId);

        if (linkedUser.isPresent()) {
            User user = linkedUser.get();

            if (!user.isEnabled()) {
                throw new UnauthorizedException(
                        "This account has been deactivated."
                );
            }

            return tokens(user);
        }

        // Never automatically link an existing account by email alone.
        Optional<User> existingUser =
                userRepository.findByEmailIgnoreCase(email);

        if (existingUser.isPresent()) {
            throw new UnauthorizedException(
                    "An account already exists with this email. " +
                            "Please sign in using your existing login method."
            );
        }

        String firstName = googleName(payload, "given_name");
        String lastName = googleName(payload, "family_name");

        if (firstName.isBlank()) {
            firstName = "Google";
        }

        if (lastName.isBlank()) {
            lastName = "User";
        }

        User user = new User(
                firstName,
                lastName,
                email,
                passwordEncoder.encode(UUID.randomUUID().toString()),
                Role.CUSTOMER
        );

        user.setGoogleId(googleId);
        user.setFullName((firstName + " " + lastName).trim());

        String picture = googleName(payload, "picture");
        if (!picture.isBlank()) {
            user.setProfileImage(picture);
        }

        userRepository.save(user);

        return tokens(user);
    }

    private String googleName(
            GoogleIdToken.Payload payload,
            String key
    ) {
        Object value = payload.get(key);
        return value instanceof String text ? text.trim() : "";
    }

    public UserResponse me(User user) {
        if (user == null) {
            throw new UnauthorizedException("Authentication required.");
        }
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateProfile(User user, UpdateProfileRequest request) {
        if (user == null) {
            throw new UnauthorizedException("Authentication required.");
        }
        User managed = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        if (request.firstName() != null && !request.firstName().isBlank()) {
            managed.setFirstName(request.firstName().trim());
        }
        if (request.lastName() != null && !request.lastName().isBlank()) {
            managed.setLastName(request.lastName().trim());
        }
        if (request.fullName() != null && !request.fullName().isBlank()) {
            managed.setFullName(request.fullName().trim());
        }
        if (request.phoneNumber() != null) {
            managed.setPhoneNumber(request.phoneNumber());
        }
        if (request.profileImage() != null) {
            managed.setProfileImage(request.profileImage());
        }
        return UserResponse.from(managed);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmailIgnoreCase(request.email().trim()).ifPresent(user -> {
            resetTokens.deleteByUserId(user.getId());
            String token = UUID.randomUUID().toString();
            resetTokens.save(new com.cosmetics.ecommerce.entity.PasswordResetToken(token, user, Instant.now().plus(30, ChronoUnit.MINUTES)));
            emailService.sendPasswordReset(user.getEmail(), token);
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        var token = resetTokens.findByToken(request.token()).orElseThrow(() -> new UnauthorizedException("Reset token is invalid or expired."));
        if (token.expired()) throw new UnauthorizedException("Reset token is invalid or expired.");
        token.getUser().setPassword(passwordEncoder.encode(request.newPassword()));
        resetTokens.delete(token);
    }

    public AuthDtos.AuthResponse refresh(AuthDtos.RefreshTokenRequest request) {
        if (!jwtService.isRefreshToken(request.refreshToken())) {
            throw new UnauthorizedException("Refresh token is invalid or expired.");
        }
        String email;
        try { email = jwtService.subject(request.refreshToken()); }
        catch (RuntimeException ex) { throw new UnauthorizedException("Refresh token is invalid or expired."); }
        User user = userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new UnauthorizedException("Refresh token is invalid."));
        if (!user.isEnabled()) {
            throw new UnauthorizedException("This account has been deactivated.");
        }
        if (!jwtService.isValid(request.refreshToken(), user)) throw new UnauthorizedException("Refresh token is invalid or expired.");
        return tokens(user);
    }

    private AuthDtos.AuthResponse tokens(User user) {
        return new AuthDtos.AuthResponse(jwtService.accessToken(user), jwtService.refreshToken(user), UserResponse.from(user));
    }
}