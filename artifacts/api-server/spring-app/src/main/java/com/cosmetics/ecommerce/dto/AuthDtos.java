package com.cosmetics.ecommerce.dto;

import jakarta.validation.constraints.*;

public final class AuthDtos {
    private AuthDtos() {}
    public record RegisterRequest(String firstName,
                                  String lastName,
                                  String fullName,
                                  @NotBlank @Email @Size(max = 190) String email,
                                  @NotBlank @Size(min = 8, max = 72) String password,
                                  @Size(max = 30) String phoneNumber,
                                  String referralCode) {
        public RegisterRequest(String firstName, String lastName, String email, String password, String phoneNumber) {
            this(firstName, lastName, null, email, password, phoneNumber, null);
        }

        public RegisterRequest(String firstName, String lastName, String fullName, String email, String password, String phoneNumber) {
            this(firstName, lastName, fullName, email, password, phoneNumber, null);
        }

        public String effectiveFirstName() {
            if (firstName != null && !firstName.isBlank()) return firstName.trim();
            if (fullName != null && !fullName.isBlank()) {
                String[] parts = fullName.trim().split("\\s+", 2);
                return parts[0];
            }
            return "User";
        }

        public String effectiveLastName() {
            if (lastName != null && !lastName.isBlank()) return lastName.trim();
            if (fullName != null && !fullName.isBlank()) {
                String[] parts = fullName.trim().split("\\s+", 2);
                return parts.length > 1 ? parts[1] : parts[0];
            }
            return "User";
        }

        public String effectiveFullName() {
            if (fullName != null && !fullName.isBlank()) return fullName.trim();
            return (effectiveFirstName() + " " + effectiveLastName()).trim();
        }
    }
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}
    public record RefreshTokenRequest(@NotBlank String refreshToken) {}
    public record AuthResponse(String accessToken, String refreshToken, UserResponse user) {}
}