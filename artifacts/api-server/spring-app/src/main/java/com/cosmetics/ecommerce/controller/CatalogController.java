package com.cosmetics.ecommerce.controller;

import com.cosmetics.ecommerce.dto.*;
import com.cosmetics.ecommerce.dto.CatalogDtos.*;
import com.cosmetics.ecommerce.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CatalogController {
    private final CatalogService catalog;

    public CatalogController(CatalogService catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/categoriesASC")
    public ApiResponse<PageResponse<CategoryResponse>> categories(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ApiResponse.ok("Categories fetched successfully.", catalog.categories(pageable));
    }

    @GetMapping("/categories")
    public ApiResponse <List<CategoryResponse>> getAllCategory(){
        return  ApiResponse.ok("Category fetched successfully.", catalog.getAllCategory());
    }

    @GetMapping("/categories/{id}")
    public ApiResponse<CategoryResponse> category(@PathVariable Long id) {
        return ApiResponse.ok("Category fetched successfully.", catalog.category(id));
    }

    @GetMapping("/brands")
    public ApiResponse<PageResponse<BrandResponse>> brands(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ApiResponse.ok("Brands fetched successfully.", catalog.brands(pageable));
    }

    @GetMapping("/brands/{id}")
    public ApiResponse<BrandResponse> brand(@PathVariable Long id) {
        return ApiResponse.ok("Brand fetched successfully.", catalog.brand(id));
    }

    @PostMapping("/admin/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest r) {
        return ApiResponse.ok("Category created successfully.", catalog.createCategory(r));
    }

    @PutMapping("/admin/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest r) {
        return ApiResponse.ok("Category updated successfully.", catalog.updateCategory(id, r));
    }

    @DeleteMapping("/admin/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        catalog.deleteCategory(id);
        return ApiResponse.ok("Category archived successfully.", null);
    }

    @PostMapping("/admin/brands")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BrandResponse> createBrand(@Valid @RequestBody BrandRequest r) {
        return ApiResponse.ok("Brand created successfully.", catalog.createBrand(r));
    }

    @PutMapping("/admin/brands/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BrandResponse> updateBrand(@PathVariable Long id, @Valid @RequestBody BrandRequest r) {
        return ApiResponse.ok("Brand updated successfully.", catalog.updateBrand(id, r));
    }

    @DeleteMapping("/admin/brands/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteBrand(@PathVariable Long id) {
        catalog.deleteBrand(id);
        return ApiResponse.ok("Brand archived successfully.", null);
    }
}