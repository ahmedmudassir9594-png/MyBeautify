package com.cosmetics.ecommerce.controller;

import com.cosmetics.ecommerce.dto.*;
import com.cosmetics.ecommerce.dto.CatalogDtos.*;
import com.cosmetics.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService products;
    public ProductController(ProductService products) { this.products = products; }
    @GetMapping public ApiResponse<PageResponse<ProductResponse>> list(@RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId, @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Boolean featured, @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice, @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.ok("Products fetched successfully.", products.list(query, categoryId, brandId, featured, minPrice, maxPrice, pageable));
    }
    @GetMapping("/{id}") public ApiResponse<ProductResponse> get(@PathVariable Long id) { return ApiResponse.ok("Product fetched successfully.", products.get(id)); }
    @GetMapping("/slug/{slug}") public ApiResponse<ProductResponse> slug(@PathVariable String slug) { return ApiResponse.ok("Product fetched successfully.", products.getBySlug(slug)); }
    @GetMapping("/categories/{categoryId}/products") public ApiResponse<PageResponse<ProductResponse>> categoryProducts(@PathVariable Long categoryId, @PageableDefault(size = 20) Pageable pageable) { return ApiResponse.ok("Category products fetched successfully.", products.list(null, categoryId, null, null, null, null, pageable)); }
    @GetMapping("/brands/{brandId}/products") public ApiResponse<PageResponse<ProductResponse>> brandProducts(@PathVariable Long brandId, @PageableDefault(size = 20) Pageable pageable) { return ApiResponse.ok("Brand products fetched successfully.", products.list(null, null, brandId, null, null, null, pageable)); }
    @PostMapping @PreAuthorize("hasRole('ADMIN')") public ApiResponse<ProductResponse> create(@Valid @RequestBody ProductRequest request) { return ApiResponse.ok("Product created successfully.", products.create(request)); }
    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<ProductResponse> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) { return ApiResponse.ok("Product updated successfully.", products.update(id, request)); }
    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<Void> delete(@PathVariable Long id) { products.delete(id); return ApiResponse.ok("Product archived successfully.", null); }
    @PatchMapping("/{id}/stock") @PreAuthorize("hasAnyRole('ADMIN','STAFF')") public ApiResponse<ProductResponse> stock(@PathVariable Long id, @RequestParam int quantity) { return ApiResponse.ok("Stock updated successfully.", products.updateStock(id, quantity)); }
}