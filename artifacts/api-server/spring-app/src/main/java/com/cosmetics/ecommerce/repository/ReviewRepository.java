package com.cosmetics.ecommerce.repository;

import com.cosmetics.ecommerce.entity.Review;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByProductIdAndApprovedTrueOrderByCreatedAtDesc(Long productId, Pageable pageable);
    Page<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);
    boolean existsByUserIdAndProductIdAndOrderId(Long userId, Long productId, Long orderId);
}