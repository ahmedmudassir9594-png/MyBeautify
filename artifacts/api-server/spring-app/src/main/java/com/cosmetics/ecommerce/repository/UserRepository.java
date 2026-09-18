package com.cosmetics.ecommerce.repository;

import com.cosmetics.ecommerce.entity.User;
import com.cosmetics.ecommerce.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    long countByRole(Role role);
}