package com.cosmetics.ecommerce.controller;

import com.cosmetics.ecommerce.dto.*;
import com.cosmetics.ecommerce.dto.CouponDtos.*;
import com.cosmetics.ecommerce.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class CouponController {
    private final CouponService coupons;
    public CouponController(CouponService coupons) { this.coupons = coupons; }
    @PostMapping("/coupons/apply") public ApiResponse<CouponResponse> apply(@Valid @RequestBody ApplyCouponRequest r) { return ApiResponse.ok("Coupon is valid.", coupons.apply(r)); }
    @GetMapping("/admin/coupons") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<List<CouponResponse>> list() { return ApiResponse.ok("Coupons fetched successfully.", coupons.list()); }
    @PostMapping("/admin/coupons") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<CouponResponse> create(@Valid @RequestBody CouponRequest r) { return ApiResponse.ok("Coupon created successfully.", coupons.create(r)); }
    @PutMapping("/admin/coupons/{id}") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<CouponResponse> update(@PathVariable Long id, @Valid @RequestBody CouponRequest r) { return ApiResponse.ok("Coupon updated successfully.", coupons.update(id, r)); }
    @DeleteMapping("/admin/coupons/{id}") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<Void> delete(@PathVariable Long id) { coupons.delete(id); return ApiResponse.ok("Coupon deleted successfully.", null); }
}