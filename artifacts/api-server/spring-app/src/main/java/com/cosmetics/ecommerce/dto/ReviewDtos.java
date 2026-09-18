package com.cosmetics.ecommerce.dto;

import com.cosmetics.ecommerce.entity.Review;
import jakarta.validation.constraints.*;
import java.time.Instant;

public final class ReviewDtos {
    private ReviewDtos() {}
    public record ReviewRequest(@Min(1) @Max(5) int rating, @NotBlank @Size(max = 2000) String comment, @NotNull Long orderId) {}
    public record ReviewResponse(Long id, Long productId, String productName, String customerName, int rating,
                                 String comment, boolean approved, Instant createdAt) {
        public static ReviewResponse from(Review r) { return new ReviewResponse(r.getId(), r.getProduct().getId(), r.getProduct().getName(),
                r.getUser().getFirstName() + " " + r.getUser().getLastName(), r.getRating(), r.getComment(), r.isApproved(), r.getCreatedAt()); }
    }
}