package com.cosmetics.ecommerce.service;

import com.cosmetics.ecommerce.dto.OrderDtos.*;
import com.cosmetics.ecommerce.entity.*;
import com.cosmetics.ecommerce.exception.ResourceNotFoundException;
import com.cosmetics.ecommerce.repository.AddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AddressService {
    private final AddressRepository addresses;
    public AddressService(AddressRepository addresses) { this.addresses = addresses; }
    public List<AddressResponse> list(User user) { return addresses.findByUserIdOrderByIsDefaultDescIdDesc(user.getId()).stream().map(AddressResponse::from).toList(); }
    @Transactional public AddressResponse create(User user, AddressRequest request) {
        if (request.isDefault()) addresses.findByUserIdOrderByIsDefaultDescIdDesc(user.getId()).forEach(a -> a.setDefault(false));
        Address a = new Address(user, request.fullName(), request.phoneNumber(), request.addressLine1(), request.city(), request.postalCode(), request.country());
        a.setAddressLine2(request.addressLine2()); a.setState(request.state()); a.setAddressType(request.addressType()); a.setDefault(request.isDefault());
        return AddressResponse.from(addresses.save(a));
    }
    @Transactional public AddressResponse update(User user, Long id, AddressRequest request) {
        Address a = addresses.findByIdAndUserId(id, user.getId()).orElseThrow(() -> new ResourceNotFoundException("Address not found."));
        if (request.isDefault() && !a.isDefault()) {
            addresses.findByUserIdOrderByIsDefaultDescIdDesc(user.getId()).forEach(existing -> existing.setDefault(false));
        }
        a.setFullName(request.fullName());
        a.setPhoneNumber(request.phoneNumber());
        a.setAddressLine1(request.addressLine1());
        a.setAddressLine2(request.addressLine2());
        a.setCity(request.city());
        a.setState(request.state());
        a.setPostalCode(request.postalCode());
        a.setCountry(request.country());
        a.setAddressType(request.addressType());
        a.setDefault(request.isDefault());
        return AddressResponse.from(a);
    }
    @Transactional public void delete(User user, Long id) { addresses.delete(addresses.findByIdAndUserId(id, user.getId()).orElseThrow(() -> new ResourceNotFoundException("Address not found."))); }
}