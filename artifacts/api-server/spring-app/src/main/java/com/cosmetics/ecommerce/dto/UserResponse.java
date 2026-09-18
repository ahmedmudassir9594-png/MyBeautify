package com.cosmetics.ecommerce.dto;

import com.cosmetics.ecommerce.entity.User;
import com.cosmetics.ecommerce.enums.Role;
import java.time.Instant;

public record UserResponse(Long id, String firstName, String lastName, String email, String phoneNumber,
                           Role role, String profileImage, Instant createdAt) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getPhoneNumber(), user.getRole(), user.getProfileImage(), user.getCreatedAt());
    }
}