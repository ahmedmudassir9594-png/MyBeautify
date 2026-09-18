package com.cosmetics.ecommerce.controller;

import com.cosmetics.ecommerce.dto.*;
import com.cosmetics.ecommerce.dto.CartDtos.*;
import com.cosmetics.ecommerce.entity.User;
import com.cosmetics.ecommerce.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cart;
    public CartController(CartService cart) { this.cart = cart; }
    @GetMapping public ApiResponse<CartResponse> get(@AuthenticationPrincipal User user) { return ApiResponse.ok("Cart fetched successfully.", cart.get(user)); }
    @PostMapping("/items") public ApiResponse<CartResponse> add(@AuthenticationPrincipal User user, @Valid @RequestBody CartItemRequest request) { return ApiResponse.ok("Item added to cart.", cart.add(user, request)); }
    @PutMapping("/items/{itemId}") public ApiResponse<CartResponse> update(@AuthenticationPrincipal User user, @PathVariable Long itemId, @RequestParam @Min(1) int quantity) { return ApiResponse.ok("Cart updated successfully.", cart.update(user, itemId, quantity)); }
    @DeleteMapping("/items/{itemId}") public ApiResponse<Void> remove(@AuthenticationPrincipal User user, @PathVariable Long itemId) { cart.remove(user, itemId); return ApiResponse.ok("Item removed from cart.", null); }
    @DeleteMapping("/clear") public ApiResponse<Void> clear(@AuthenticationPrincipal User user) { cart.clear(user); return ApiResponse.ok("Cart cleared successfully.", null); }
}