package com.cosmetics.ecommerce.entity;

import com.cosmetics.ecommerce.enums.Role;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users", indexes = @Index(name = "idx_user_email", columnList = "email", unique = true))
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Column(nullable = false, unique = true, length = 190)
    private String email;
    @Column(name = "google_id", unique = true, length = 255)
    private String googleId;
    @Column(nullable = false)
    private String password;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.CUSTOMER;
    private String profileImage;
    @Column(nullable = false)
    private boolean enabled = true;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;
    @Column(name = "referral_code", unique = true, length = 40)
    private String referralCode;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referred_by_id")
    private User referredBy;

    protected User() {}

    public User(String firstName, String lastName, String email, String password, Role role) {
        this.firstName = firstName != null ? firstName : "";
        this.lastName = lastName != null ? lastName : "";
        this.fullName = ((this.firstName + " " + this.lastName)).trim();
        if (this.fullName.isEmpty()) this.fullName = "User";
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(String fullName, String email, String password, Role role) {
        this.fullName = fullName != null && !fullName.isBlank() ? fullName.trim() : "User";
        String[] parts = this.fullName.split("\\s+", 2);
        this.firstName = parts[0];
        this.lastName = parts.length > 1 ? parts[1] : parts[0];
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override public String getUsername() { return email; }
    @Override public String getPassword() { return password; }
    @Override public boolean isEnabled() { return enabled; }
    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() {
        if (fullName != null && !fullName.isBlank()) return fullName;
        return ((firstName != null ? firstName : "") + (lastName != null && !lastName.isBlank() ? " " + lastName : "")).trim();
    }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public Role getRole() { return role; }
    public String getProfileImage() { return profileImage; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.fullName = getFullName();
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.fullName = getFullName();
    }
    public void setFullName(String fullName) {
        this.fullName = fullName != null && !fullName.isBlank() ? fullName.trim() : "";
        if (!this.fullName.isEmpty()) {
            String[] parts = this.fullName.split("\\s+", 2);
            if (this.firstName == null || this.firstName.isBlank()) this.firstName = parts[0];
            if (this.lastName == null || this.lastName.isBlank()) this.lastName = parts.length > 1 ? parts[1] : parts[0];
        }
    }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public void setPassword(String password) { this.password = password; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setRole(Role role) { this.role = role; }
    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }
    public User getReferredBy() { return referredBy; }
    public void setReferredBy(User referredBy) { this.referredBy = referredBy; }
    public String getGoogleId() {return googleId;}
    public void setGoogleId(String googleId) {this.googleId = googleId;}

}