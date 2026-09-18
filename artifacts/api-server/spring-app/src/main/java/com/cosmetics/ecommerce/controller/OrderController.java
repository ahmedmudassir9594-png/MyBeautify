package com.cosmetics.ecommerce.controller;

import com.cosmetics.ecommerce.dto.*;
import com.cosmetics.ecommerce.dto.OrderDtos.*;
import com.cosmetics.ecommerce.entity.User;
import com.cosmetics.ecommerce.service.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping
public class OrderController {
    private final OrderService orders; private final AddressService addresses;
    public OrderController(OrderService orders, AddressService addresses) { this.orders = orders; this.addresses = addresses; }
    @PostMapping("/orders") public ApiResponse<OrderResponse> checkout(@AuthenticationPrincipal User user, @Valid @RequestBody OrderRequest request) { return ApiResponse.ok("Order placed successfully.", orders.checkout(user, request)); }
    @GetMapping("/orders") public ApiResponse<PageResponse<OrderResponse>> list(@AuthenticationPrincipal User user, @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) { return ApiResponse.ok("Orders fetched successfully.", orders.list(user, pageable)); }
    @GetMapping("/orders/{id}") public ApiResponse<OrderResponse> get(@AuthenticationPrincipal User user, @PathVariable Long id) { return ApiResponse.ok("Order fetched successfully.", orders.get(user, id)); }
    @GetMapping("/orders/number/{orderNumber}") public ApiResponse<OrderResponse> number(@AuthenticationPrincipal User user, @PathVariable String orderNumber) { return ApiResponse.ok("Order fetched successfully.", orders.byNumber(user, orderNumber)); }
    @PostMapping("/orders/{id}/cancel") public ApiResponse<OrderResponse> cancel(@AuthenticationPrincipal User user, @PathVariable Long id) { return ApiResponse.ok("Order cancelled successfully.", orders.cancel(user, id)); }
    @GetMapping("/addresses") public ApiResponse<List<AddressResponse>> addresses(@AuthenticationPrincipal User user) { return ApiResponse.ok("Addresses fetched successfully.", addresses.list(user)); }
    @PostMapping("/addresses") public ApiResponse<AddressResponse> addAddress(@AuthenticationPrincipal User user, @Valid @RequestBody AddressRequest request) { return ApiResponse.ok("Address added successfully.", addresses.create(user, request)); }
    @PutMapping("/addresses/{id}") public ApiResponse<AddressResponse> updateAddress(@AuthenticationPrincipal User user, @PathVariable Long id, @Valid @RequestBody AddressRequest request) { return ApiResponse.ok("Address updated successfully.", addresses.update(user, id, request)); }
    @DeleteMapping("/addresses/{id}") public ApiResponse<Void> deleteAddress(@AuthenticationPrincipal User user, @PathVariable Long id) { addresses.delete(user, id); return ApiResponse.ok("Address deleted successfully.", null); }
}