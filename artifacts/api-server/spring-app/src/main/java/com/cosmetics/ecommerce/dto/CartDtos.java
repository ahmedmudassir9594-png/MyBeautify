package com.cosmetics.ecommerce.dto;

import com.cosmetics.ecommerce.entity.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public final class CartDtos {
    private CartDtos() {}
    public record CartItemRequest(@NotNull Long productId, @Min(1) @Max(999) int quantity) {}
    public record CartItemResponse(Long id, Long productId, String productName, String imageUrl, int quantity,
                                   BigDecimal price, BigDecimal subtotal, int availableStock) {
        public static CartItemResponse from(CartItem item) {
            Product p = item.getProduct();
            return new CartItemResponse(item.getId(), p.getId(), p.getName(), p.getImageUrl(), item.getQuantity(),
                    item.getPrice(), item.subtotal(), p.getStockQuantity());
        }
    }
    public record CartResponse(Long id, List<CartItemResponse> items, BigDecimal subtotal) {
        public static CartResponse from(Cart cart) {
            List<CartItemResponse> items = cart.getItems().stream().map(CartItemResponse::from).toList();
            return new CartResponse(cart.getId(), items, items.stream().map(CartItemResponse::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
    }
}