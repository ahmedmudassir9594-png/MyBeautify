package com.cosmetics.ecommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class ReferralDtos {
    private ReferralDtos() {}

    public record ReferralSummaryResponse(
        String referralCode,
        String referralLink,
        String discountDescription,
        BigDecimal discountPercent,
        BigDecimal minimumOrderAmount,
        long totalReferrals,
        long successfulReferrals,
        BigDecimal totalRewards,
        List<ReferralItemDto> recentReferrals
    ) {}

    public record ReferralItemDto(
        Long id,
        String friendName,
        String friendEmailMasked,
        String status,
        Instant registeredAt,
        Instant completedAt
    ) {}

    public record ValidateReferralResponse(
        boolean valid,
        String referralCode,
        String referrerName,
        String discountDescription,
        BigDecimal discountPercent,
        String message
    ) {}

    public record ReferralDiscountInfo(
        boolean eligible,
        String code,
        BigDecimal discountPercent,
        BigDecimal discountAmount,
        String description
    ) {}
}
