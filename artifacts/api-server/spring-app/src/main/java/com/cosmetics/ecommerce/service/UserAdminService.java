package com.cosmetics.ecommerce.service;

import com.cosmetics.ecommerce.dto.ProfileDtos.AdminUserUpdateRequest;
import com.cosmetics.ecommerce.dto.UserResponse;
import com.cosmetics.ecommerce.entity.User;
import com.cosmetics.ecommerce.enums.Role;
import com.cosmetics.ecommerce.exception.BadRequestException;
import com.cosmetics.ecommerce.exception.ResourceNotFoundException;
import com.cosmetics.ecommerce.dto.PageResponse;
import com.cosmetics.ecommerce.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAdminService {
    private final UserRepository users;
    public UserAdminService(UserRepository users) { this.users = users; }
    public PageResponse<UserResponse> list(Pageable pageable) { return PageResponse.from(users.findAll(pageable).map(UserResponse::from)); }
    @Transactional public UserResponse update(Long id, AdminUserUpdateRequest request) {
        User user = users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found."));
        if (user.getRole() == Role.ADMIN && (request.role() != Role.ADMIN || !request.enabled())) {
            long remainingActiveAdmins = users.findAll().stream()
                    .filter(u -> u.getRole() == Role.ADMIN && u.isEnabled() && !u.getId().equals(id))
                    .count();
            if (remainingActiveAdmins == 0) {
                throw new BadRequestException("Cannot disable or demote the only remaining active administrator.");
            }
        }
        user.setRole(request.role()); user.setEnabled(request.enabled()); return UserResponse.from(user);
    }
}