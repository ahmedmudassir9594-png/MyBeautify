package com.cosmetics.ecommerce.service;

import com.cosmetics.ecommerce.dto.CouponDtos.*;
import com.cosmetics.ecommerce.entity.Coupon;
import com.cosmetics.ecommerce.enums.DiscountType;
import com.cosmetics.ecommerce.exception.*;
import com.cosmetics.ecommerce.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Service
public class CouponService {
    private final CouponRepository coupons;
    public CouponService(CouponRepository coupons) { this.coupons = coupons; }

    public CouponResponse apply(ApplyCouponRequest request) {
        Coupon coupon = valid(request.code(), request.orderAmount());
        return CouponResponse.from(coupon);
    }
    public List<CouponResponse> list() { return coupons.findAll().stream().map(CouponResponse::from).toList(); }
    @Transactional public CouponResponse create(CouponRequest r) {
        if (coupons.findByCodeIgnoreCase(r.code()).isPresent()) throw new DuplicateResourceException("Coupon code is already in use.");
        Coupon c = new Coupon(r.code().trim().toUpperCase(), r.discountType(), r.discountValue(), r.startDate(), r.expiryDate());
        copy(r, c); return CouponResponse.from(coupons.save(c));
    }
    @Transactional public CouponResponse update(Long id, CouponRequest r) {
        Coupon c = coupons.findById(id).orElseThrow(() -> new ResourceNotFoundException("Coupon not found."));
        copy(r, c); return CouponResponse.from(c);
    }
    @Transactional public void delete(Long id) { coupons.delete(coupons.findById(id).orElseThrow(() -> new ResourceNotFoundException("Coupon not found."))); }
    private Coupon valid(String code, BigDecimal amount) {
        Coupon c = coupons.findByCodeIgnoreCase(code.trim()).orElseThrow(() -> new InvalidCouponException("Coupon is invalid."));
        if (!c.availableAt(Instant.now())) throw new InvalidCouponException("Coupon is expired or unavailable.");
        if (amount.compareTo(c.getMinimumOrderAmount()) < 0) throw new InvalidCouponException("Order does not meet the coupon minimum.");
        return c;
    }
    private void copy(CouponRequest r, Coupon c) {
        c.setDescription(r.description()); c.setMinimumOrderAmount(r.minimumOrderAmount() == null ? BigDecimal.ZERO : r.minimumOrderAmount());
        c.setMaximumDiscount(r.maximumDiscount()); c.setUsageLimit(r.usageLimit()); c.setActive(r.active() == null || r.active());
    }
}