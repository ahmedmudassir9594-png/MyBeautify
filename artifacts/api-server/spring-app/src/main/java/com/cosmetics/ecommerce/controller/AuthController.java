package com.cosmetics.ecommerce.controller;

import com.cosmetics.ecommerce.dto.*;
import com.cosmetics.ecommerce.dto.AuthDtos.*;
import com.cosmetics.ecommerce.dto.ProfileDtos.*;
import com.cosmetics.ecommerce.entity.User;
import com.cosmetics.ecommerce.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService auth;
    public AuthController(AuthService auth) { this.auth = auth; }
    @PostMapping("/register") public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {return ApiResponse.ok("Registration successful.", auth.register(request)); }
    @PostMapping("/login") public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) { return ApiResponse.ok("Login successful.", auth.login(request)); }
    @PostMapping("/refresh-token") public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) { return ApiResponse.ok("Token refreshed.", auth.refresh(request)); }
    @GetMapping("/me") public ApiResponse<UserResponse> me(@AuthenticationPrincipal User user) { return ApiResponse.ok("Profile fetched successfully.", auth.me(user)); }
    @PutMapping("/me") public ApiResponse<UserResponse> update(@AuthenticationPrincipal User user, @Valid @RequestBody UpdateProfileRequest request) { return ApiResponse.ok("Profile updated successfully.", auth.updateProfile(user, request)); }
    @PostMapping("/forgot-password") public ApiResponse<Void> forgot(@Valid @RequestBody ForgotPasswordRequest request) { auth.forgotPassword(request); return ApiResponse.ok("If that email exists, reset instructions have been sent.", null); }
    @PostMapping("/reset-password") public ApiResponse<Void> reset(@Valid @RequestBody ResetPasswordRequest request) { auth.resetPassword(request); return ApiResponse.ok("Password reset successfully.", null); }
}