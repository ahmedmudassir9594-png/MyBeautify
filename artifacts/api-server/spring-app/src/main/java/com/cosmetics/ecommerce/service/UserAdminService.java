package com.cosmetics.ecommerce.service;

import com.cosmetics.ecommerce.dto.ProfileDtos.AdminUserUpdateRequest;
import com.cosmetics.ecommerce.dto.UserResponse;
import com.cosmetics.ecommerce.entity.User;
import com.cosmetics.ecommerce.exception.ResourceNotFoundException;
import com.cosmetics.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UserAdminService {
    private final UserRepository users;
    public UserAdminService(UserRepository users) { this.users = users; }
    public List<UserResponse> list() { return users.findAll().stream().map(UserResponse::from).toList(); }
    @Transactional public UserResponse update(Long id, AdminUserUpdateRequest request) {
        User user = users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found."));
        user.setRole(request.role()); user.setEnabled(request.enabled()); return UserResponse.from(user);
    }
}