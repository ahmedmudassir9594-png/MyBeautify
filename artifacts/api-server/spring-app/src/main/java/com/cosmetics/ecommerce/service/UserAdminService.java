package com.cosmetics.ecommerce.service;

import com.cosmetics.ecommerce.dto.ProfileDtos.AdminUserUpdateRequest;
import com.cosmetics.ecommerce.dto.UserResponse;
import com.cosmetics.ecommerce.entity.User;
import com.cosmetics.ecommerce.enums.AuditAction;
import com.cosmetics.ecommerce.enums.AuditResult;
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
    private final AuditLogService auditLogService;

    @org.springframework.beans.factory.annotation.Autowired
    public UserAdminService(UserRepository users, AuditLogService auditLogService) {
        this.users = users;
        this.auditLogService = auditLogService;
    }

    public UserAdminService(UserRepository users) {
        this(users, null);
    }

    public PageResponse<UserResponse> list(Pageable pageable) {
        return PageResponse.from(users.findAll(pageable).map(UserResponse::from));
    }

    @Transactional
    public UserResponse update(Long id, AdminUserUpdateRequest request) {
        User user = users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found."));
        if (user.getRole() == Role.ADMIN && (request.role() != Role.ADMIN || !request.enabled())) {
            long remainingActiveAdmins = users.findAll().stream()
                    .filter(u -> u.getRole() == Role.ADMIN && u.isEnabled() && !u.getId().equals(id))
                    .count();
            if (remainingActiveAdmins == 0) {
                throw new BadRequestException("Cannot disable or demote the only remaining active administrator.");
            }
        }

        Role oldRole = user.getRole();
        boolean oldEnabled = user.isEnabled();

        user.setRole(request.role());
        user.setEnabled(request.enabled());

        if (auditLogService != null) {
            String prev = "{\"role\":\"" + oldRole + "\",\"enabled\":" + oldEnabled + "}";
            String next = "{\"role\":\"" + request.role() + "\",\"enabled\":" + request.enabled() + "}";

            if (oldRole != request.role()) {
                auditLogService.logEvent(AuditAction.USER_ROLE_CHANGE, AuditResult.SUCCESS, "USER", String.valueOf(user.getId()),
                        "User " + user.getEmail() + " role changed from " + oldRole + " to " + request.role(), prev, next);
            }
            if (oldEnabled != request.enabled()) {
                auditLogService.logEvent(AuditAction.USER_STATUS_CHANGE, AuditResult.SUCCESS, "USER", String.valueOf(user.getId()),
                        "User " + user.getEmail() + " status changed from " + (oldEnabled ? "ENABLED" : "DISABLED") +
                                " to " + (request.enabled() ? "ENABLED" : "DISABLED"), prev, next);
            }
            if (oldRole == request.role() && oldEnabled == request.enabled()) {
                auditLogService.logEvent(AuditAction.USER_UPDATE, AuditResult.SUCCESS, "USER", String.valueOf(user.getId()),
                        "User " + user.getEmail() + " updated by admin", prev, next);
            }
        }

        return UserResponse.from(user);
    }
}
