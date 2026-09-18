package com.cosmetics.ecommerce.controller;

import com.cosmetics.ecommerce.dto.*;
import com.cosmetics.ecommerce.dto.ReviewDtos.*;
import com.cosmetics.ecommerce.entity.User;
import com.cosmetics.ecommerce.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReviewController {
    private final ReviewService reviews;
    public ReviewController(ReviewService reviews) { this.reviews = reviews; }
    @PostMapping("/products/{productId}/reviews") public ApiResponse<ReviewResponse> create(@AuthenticationPrincipal User user, @PathVariable Long productId, @Valid @RequestBody ReviewRequest request) { return ApiResponse.ok("Review submitted for approval.", reviews.create(user, productId, request)); }
    @GetMapping("/products/{productId}/reviews") public ApiResponse<PageResponse<ReviewResponse>> list(@PathVariable Long productId, @PageableDefault(size = 20) Pageable pageable) { return ApiResponse.ok("Reviews fetched successfully.", reviews.productReviews(productId, pageable)); }
    @PutMapping("/reviews/{reviewId}") public ApiResponse<ReviewResponse> update(@AuthenticationPrincipal User user, @PathVariable Long reviewId, @Valid @RequestBody ReviewRequest request) { return ApiResponse.ok("Review updated and sent for approval.", reviews.update(user, reviewId, request)); }
    @DeleteMapping("/reviews/{reviewId}") public ApiResponse<Void> delete(@AuthenticationPrincipal User user, @PathVariable Long reviewId) { reviews.delete(user, reviewId); return ApiResponse.ok("Review deleted successfully.", null); }
    @GetMapping("/admin/reviews") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<PageResponse<ReviewResponse>> all(@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) { return ApiResponse.ok("Reviews fetched successfully.", reviews.all(pageable)); }
    @PatchMapping("/admin/reviews/{id}/approve") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<ReviewResponse> approve(@PathVariable Long id) { return ApiResponse.ok("Review approved.", reviews.approve(id, true)); }
    @PatchMapping("/admin/reviews/{id}/reject") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<ReviewResponse> reject(@PathVariable Long id) { return ApiResponse.ok("Review rejected.", reviews.approve(id, false)); }
}