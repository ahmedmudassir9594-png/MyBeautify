package com.cosmetics.ecommerce.repository;

import com.cosmetics.ecommerce.entity.Referral;
import com.cosmetics.ecommerce.entity.User;
import com.cosmetics.ecommerce.enums.ReferralStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {
    long countByReferrer(User referrer);
    long countByReferrerAndStatus(User referrer, ReferralStatus status);
    List<Referral> findByReferrerOrderByCreatedAtDesc(User referrer, Pageable pageable);
    Optional<Referral> findByReferredUser(User referredUser);
    boolean existsByReferredUser(User referredUser);
    boolean existsByReferrerAndReferredUser(User referrer, User referredUser);
}
