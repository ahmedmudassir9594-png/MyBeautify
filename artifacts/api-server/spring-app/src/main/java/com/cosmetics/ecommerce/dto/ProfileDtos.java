package com.cosmetics.ecommerce.dto;

import jakarta.validation.constraints.*;
import com.cosmetics.ecommerce.enums.Role;

public final class ProfileDtos {
    private ProfileDtos() {}
    public record UpdateProfileRequest(@NotBlank String firstName, @NotBlank String lastName, @Size(max = 30) String phoneNumber, String profileImage) {}
    public record ForgotPasswordRequest(@NotBlank @Email String email) {}
    public record ResetPasswordRequest(@NotBlank String token, @NotBlank @Size(min = 8, max = 72) String newPassword) {}
    public record AdminUserUpdateRequest(@NotNull Role role, boolean enabled) {}
}