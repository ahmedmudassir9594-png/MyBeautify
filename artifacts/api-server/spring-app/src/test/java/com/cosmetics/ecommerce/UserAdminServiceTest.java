package com.cosmetics.ecommerce;

import com.cosmetics.ecommerce.dto.ProfileDtos.AdminUserUpdateRequest;
import com.cosmetics.ecommerce.dto.UserResponse;
import com.cosmetics.ecommerce.entity.User;
import com.cosmetics.ecommerce.enums.Role;
import com.cosmetics.ecommerce.exception.BadRequestException;
import com.cosmetics.ecommerce.repository.UserRepository;
import com.cosmetics.ecommerce.service.UserAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAdminService userAdminService;

    private User soleAdmin;
    private User secondAdmin;

    @BeforeEach
    void setUp() {
        soleAdmin = new User("Ava", "Admin", "admin1@example.com", "hash", Role.ADMIN);
        ReflectionTestUtils.setField(soleAdmin, "id", 1L);
        soleAdmin.setEnabled(true);

        secondAdmin = new User("Bob", "Admin", "admin2@example.com", "hash", Role.ADMIN);
        ReflectionTestUtils.setField(secondAdmin, "id", 2L);
        secondAdmin.setEnabled(true);
    }

    @Test
    @DisplayName("Prevent demoting the sole remaining active administrator")
    void cannotDemoteSoleActiveAdmin() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(soleAdmin));
        // Only soleAdmin exists in the repository
        when(userRepository.findAll()).thenReturn(List.of(soleAdmin));

        AdminUserUpdateRequest request = new AdminUserUpdateRequest(Role.CUSTOMER, true);

        assertThatThrownBy(() -> userAdminService.update(1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Cannot disable or demote the only remaining active administrator.");
    }

    @Test
    @DisplayName("Prevent deactivating the sole remaining active administrator")
    void cannotDeactivateSoleActiveAdmin() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(soleAdmin));
        when(userRepository.findAll()).thenReturn(List.of(soleAdmin));

        AdminUserUpdateRequest request = new AdminUserUpdateRequest(Role.ADMIN, false);

        assertThatThrownBy(() -> userAdminService.update(1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Cannot disable or demote the only remaining active administrator.");
    }

    @Test
    @DisplayName("Allows demoting or deactivating an admin if another active administrator exists")
    void canDemoteAdminIfAnotherAdminExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(soleAdmin));
        when(userRepository.findAll()).thenReturn(List.of(soleAdmin, secondAdmin));

        AdminUserUpdateRequest request = new AdminUserUpdateRequest(Role.STAFF, true);

        UserResponse response = userAdminService.update(1L, request);

        assertThat(response.role()).isEqualTo(Role.STAFF);
        assertThat(soleAdmin.getRole()).isEqualTo(Role.STAFF);
    }
}
