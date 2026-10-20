package com.cosmetics.ecommerce.entity;

import com.cosmetics.ecommerce.enums.AddressType;
import jakarta.persistence.*;

@Entity
@Table(name = "addresses", indexes = @Index(name = "idx_address_user", columnList = "user_id"))
public class Address {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private User user;
    @Column(nullable = false) private String fullName;
    @Column(nullable = false) private String phoneNumber;
    @Column(nullable = false) private String addressLine1;
    private String addressLine2;
    @Column(nullable = false) private String city;
    private String state;
    @Column(nullable = false) private String postalCode;
    @Column(nullable = false) private String country;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private AddressType addressType = AddressType.HOME;
    @Column(nullable = false) private boolean isDefault;
    protected Address() {}
    public Address(User user, String fullName, String phoneNumber, String addressLine1, String city, String postalCode, String country) {
        this.user = user; this.fullName = fullName; this.phoneNumber = phoneNumber; this.addressLine1 = addressLine1;
        this.city = city; this.postalCode = postalCode; this.country = country;
    }
    public Long getId() { return id; } public User getUser() { return user; } public String getFullName() { return fullName; }
    public String getPhoneNumber() { return phoneNumber; } public String getAddressLine1() { return addressLine1; }
    public String getAddressLine2() { return addressLine2; } public String getCity() { return city; } public String getState() { return state; }
    public String getPostalCode() { return postalCode; } public String getCountry() { return country; } public AddressType getAddressType() { return addressType; }
    public boolean isDefault() { return isDefault; }
    public void setFullName(String v) { fullName = v; }
    public void setPhoneNumber(String v) { phoneNumber = v; }
    public void setAddressLine1(String v) { addressLine1 = v; }
    public void setAddressLine2(String v) { addressLine2 = v; }
    public void setCity(String v) { city = v; }
    public void setState(String v) { state = v; }
    public void setPostalCode(String v) { postalCode = v; }
    public void setCountry(String v) { country = v; }
    public void setAddressType(AddressType v) { addressType = v; }
    public void setDefault(boolean v) { isDefault = v; }
}