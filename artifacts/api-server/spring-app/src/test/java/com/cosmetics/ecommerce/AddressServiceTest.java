package com.cosmetics.ecommerce;

import com.cosmetics.ecommerce.dto.OrderDtos.AddressRequest;
import com.cosmetics.ecommerce.dto.OrderDtos.AddressResponse;
import com.cosmetics.ecommerce.entity.Address;
import com.cosmetics.ecommerce.entity.User;
import com.cosmetics.ecommerce.enums.AddressType;
import com.cosmetics.ecommerce.enums.Role;
import com.cosmetics.ecommerce.exception.ResourceNotFoundException;
import com.cosmetics.ecommerce.repository.AddressRepository;
import com.cosmetics.ecommerce.service.AddressService;
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
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    private User user;
    private Address existingAddress;

    @BeforeEach
    void setUp() {
        user = new User("Maya", "Perera", "maya@example.com", "hash", Role.CUSTOMER);
        ReflectionTestUtils.setField(user, "id", 10L);

        existingAddress = new Address(user, "Old Name", "+9411", "Old Street", "Colombo", "00100", "LK");
        ReflectionTestUtils.setField(existingAddress, "id", 1L);
    }

    @Test
    @DisplayName("Address update mutates and persists all updated fields")
    void updateAddressPersistsAllFields() {
        when(addressRepository.findByIdAndUserId(1L, 10L)).thenReturn(Optional.of(existingAddress));

        AddressRequest request = new AddressRequest(
                "New Name", "+94771234567", "456 Galle Rd", "Apt 2B",
                "Kandy", "Central", "20000", "LK", AddressType.HOME, true
        );

        when(addressRepository.findByUserIdOrderByIsDefaultDescIdDesc(10L))
                .thenReturn(List.of(existingAddress));

        AddressResponse response = addressService.update(user, 1L, request);

        assertThat(response.fullName()).isEqualTo("New Name");
        assertThat(response.phoneNumber()).isEqualTo("+94771234567");
        assertThat(response.addressLine1()).isEqualTo("456 Galle Rd");
        assertThat(response.addressLine2()).isEqualTo("Apt 2B");
        assertThat(response.city()).isEqualTo("Kandy");
        assertThat(response.state()).isEqualTo("Central");
        assertThat(response.postalCode()).isEqualTo("20000");
        assertThat(response.country()).isEqualTo("LK");
        assertThat(response.addressType()).isEqualTo(AddressType.HOME);
        assertThat(response.isDefault()).isTrue();
    }

    @Test
    @DisplayName("User cannot update an address belonging to another user (Ownership check)")
    void cannotUpdateAnotherUsersAddress() {
        when(addressRepository.findByIdAndUserId(99L, 10L)).thenReturn(Optional.empty());

        AddressRequest request = new AddressRequest(
                "Attacker", "+123", "Attacker St", null,
                "City", null, "12345", "LK", AddressType.HOME, false
        );

        assertThatThrownBy(() -> addressService.update(user, 99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Address not found.");
    }

    @Test
    @DisplayName("User cannot delete an address belonging to another user (Ownership check)")
    void cannotDeleteAnotherUsersAddress() {
        when(addressRepository.findByIdAndUserId(99L, 10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.delete(user, 99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Address not found.");
    }
}
