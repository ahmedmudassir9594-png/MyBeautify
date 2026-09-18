package com.cosmetics.ecommerce.service;

import com.cosmetics.ecommerce.dto.*;
import com.cosmetics.ecommerce.dto.ReviewDtos.*;
import com.cosmetics.ecommerce.entity.*;
import com.cosmetics.ecommerce.enums.OrderStatus;
import com.cosmetics.ecommerce.exception.*;
import com.cosmetics.ecommerce.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {
    private final ReviewRepository reviews; private final ProductRepository products; private final OrderRepository orders;
    private final OrderItemRepository orderItems;
    public ReviewService(ReviewRepository reviews, ProductRepository products, OrderRepository orders, OrderItemRepository orderItems) {
        this.reviews = reviews; this.products = products; this.orders = orders; this.orderItems = orderItems;
    }
    @Transactional
    public ReviewResponse create(User user, Long productId, ReviewRequest request) {
        Product product = products.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found."));
        Order order = orders.findByIdAndUserId(request.orderId(), user.getId()).orElseThrow(() -> new ResourceNotFoundException("Order not found."));
        if (order.getOrderStatus() != OrderStatus.DELIVERED || !orderItems.existsByProductIdAndOrderUserIdAndOrderOrderStatus(productId, user.getId(), OrderStatus.DELIVERED))
            throw new UnauthorizedException("You can review a product only after it has been delivered.");
        if (reviews.existsByUserIdAndProductIdAndOrderId(user.getId(), productId, order.getId())) throw new DuplicateResourceException("You already reviewed this purchase.");
        return ReviewResponse.from(reviews.save(new Review(user, product, order, request.rating(), request.comment())));
    }
    public PageResponse<ReviewResponse> productReviews(Long productId, Pageable pageable) {
        return PageResponse.from(reviews.findByProductIdAndApprovedTrueOrderByCreatedAtDesc(productId, pageable).map(ReviewResponse::from));
    }
    @Transactional
    public ReviewResponse update(User user, Long id, ReviewRequest request) {
        Review review = reviews.findById(id).orElseThrow(() -> new ResourceNotFoundException("Review not found."));
        if (!review.getUser().getId().equals(user.getId())) throw new UnauthorizedException("You can only edit your own reviews.");
        review.setRating(request.rating()); review.setComment(request.comment()); review.setApproved(false);
        return ReviewResponse.from(review);
    }
    @Transactional
    public void delete(User user, Long id) {
        Review review = reviews.findById(id).orElseThrow(() -> new ResourceNotFoundException("Review not found."));
        if (!review.getUser().getId().equals(user.getId())) throw new UnauthorizedException("You can only delete your own reviews.");
        reviews.delete(review);
    }
    @Transactional public ReviewResponse approve(Long id, boolean approved) {
        Review review = reviews.findById(id).orElseThrow(() -> new ResourceNotFoundException("Review not found."));
        review.setApproved(approved); return ReviewResponse.from(review);
    }
    public PageResponse<ReviewResponse> all(Pageable pageable) { return PageResponse.from(reviews.findAllByOrderByCreatedAtDesc(pageable).map(ReviewResponse::from)); }
}