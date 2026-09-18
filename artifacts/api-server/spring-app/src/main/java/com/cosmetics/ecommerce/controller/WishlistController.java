package com.cosmetics.ecommerce.controller;

import com.cosmetics.ecommerce.dto.*;
import com.cosmetics.ecommerce.dto.CatalogDtos.ProductResponse;
import com.cosmetics.ecommerce.entity.User;
import com.cosmetics.ecommerce.service.WishlistService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {
    private final WishlistService wishlist;
    public WishlistController(WishlistService wishlist) { this.wishlist = wishlist; }
    @GetMapping public ApiResponse<List<ProductResponse>> list(@AuthenticationPrincipal User user) { return ApiResponse.ok("Wishlist fetched successfully.", wishlist.list(user)); }
    @PostMapping("/{productId}") public ApiResponse<List<ProductResponse>> add(@AuthenticationPrincipal User user, @PathVariable Long productId) { return ApiResponse.ok("Product added to wishlist.", wishlist.add(user, productId)); }
    @DeleteMapping("/{productId}") public ApiResponse<Void> remove(@AuthenticationPrincipal User user, @PathVariable Long productId) { wishlist.remove(user, productId); return ApiResponse.ok("Product removed from wishlist.", null); }
}