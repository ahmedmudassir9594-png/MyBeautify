package com.cosmetics.ecommerce.service;

import com.cosmetics.ecommerce.dto.OrderDtos.*;
import com.cosmetics.ecommerce.entity.*;
import com.cosmetics.ecommerce.enums.AuditAction;
import com.cosmetics.ecommerce.enums.AuditResult;
import com.cosmetics.ecommerce.exception.ResourceNotFoundException;
import com.cosmetics.ecommerce.repository.AddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AddressService {
    private final AddressRepository addresses;
    private final AuditLogService auditLogService;

    @org.springframework.beans.factory.annotation.Autowired
    public AddressService(AddressRepository addresses, AuditLogService auditLogService) {
        this.addresses = addresses;
        this.auditLogService = auditLogService;
    }

    public AddressService(AddressRepository addresses) {
        this(addresses, null);
    }

    public List<AddressResponse> list(User user) {
        return addresses.findByUserIdOrderByIsDefaultDescIdDesc(user.getId()).stream().map(AddressResponse::from).toList();
    }

    @Transactional
    public AddressResponse create(User user, AddressRequest request) {
        if (request.isDefault()) addresses.findByUserIdOrderByIsDefaultDescIdDesc(user.getId()).forEach(a -> a.setDefault(false));
        Address a = new Address(user, request.fullName(), request.phoneNumber(), request.addressLine1(), request.city(), request.postalCode(), request.country());
        a.setAddressLine2(request.addressLine2()); a.setState(request.state()); a.setAddressType(request.addressType()); a.setDefault(request.isDefault());
        Address saved = addresses.save(a);

        if (auditLogService != null) {
            String state = String.format("{\"id\":%d,\"city\":\"%s\",\"postalCode\":\"%s\",\"addressType\":\"%s\",\"default\":%s}",
                    saved.getId(), saved.getCity(), saved.getPostalCode(), saved.getAddressType(), saved.isDefault());
            auditLogService.logEvent(AuditAction.ADDRESS_CREATE, AuditResult.SUCCESS, "ADDRESS", String.valueOf(saved.getId()),
                    "Address created for user " + user.getEmail(), null, state);
        }

        return AddressResponse.from(saved);
    }

    @Transactional
    public AddressResponse update(User user, Long id, AddressRequest request) {
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

        if (auditLogService != null) {
            String state = String.format("{\"id\":%d,\"city\":\"%s\",\"postalCode\":\"%s\",\"addressType\":\"%s\",\"default\":%s}",
                    a.getId(), a.getCity(), a.getPostalCode(), a.getAddressType(), a.isDefault());
            auditLogService.logEvent(AuditAction.ADDRESS_UPDATE, AuditResult.SUCCESS, "ADDRESS", String.valueOf(a.getId()),
                    "Address updated for user " + user.getEmail(), null, state);
        }

        return AddressResponse.from(a);
    }

    @Transactional
    public void delete(User user, Long id) {
        Address a = addresses.findByIdAndUserId(id, user.getId()).orElseThrow(() -> new ResourceNotFoundException("Address not found."));
        addresses.delete(a);

        if (auditLogService != null) {
            auditLogService.logEvent(AuditAction.ADDRESS_DELETE, AuditResult.SUCCESS, "ADDRESS", String.valueOf(id),
                    "Address deleted with ID: " + id + " for user " + user.getEmail(), null, null);
        }
    }
}
