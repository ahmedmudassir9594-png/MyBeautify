package com.cosmetics.ecommerce.dto;

import com.cosmetics.ecommerce.entity.User;
import com.cosmetics.ecommerce.enums.Role;
import java.time.Instant;

public record UserResponse(Long id, String firstName, String lastName, String fullName, String email, String phoneNumber,
                           Role role, String profileImage, Instant createdAt) {

    public UserResponse(Long id, String firstName, String lastName, String email, String phoneNumber,
                        Role role, String profileImage, Instant createdAt) {
        this(id, firstName, lastName, (firstName != null ? firstName + (lastName != null && !lastName.isBlank() ? " " + lastName : "") : "").trim(),
                email, phoneNumber, role, profileImage, createdAt);
    }

    public static UserResponse from(User user) {
        if (user == null) return null;
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getFullName(),
                user.getEmail(), user.getPhoneNumber(), user.getRole(), user.getProfileImage(), user.getCreatedAt());
    }

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    public String name() {
        return fullName != null && !fullName.isBlank() ? fullName : firstName;
    }
}