package com.cosmetics.ecommerce.service;

import com.cosmetics.ecommerce.dto.CatalogDtos.*;
import com.cosmetics.ecommerce.entity.*;
import com.cosmetics.ecommerce.exception.*;
import com.cosmetics.ecommerce.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CatalogService {
    private final CategoryRepository categories; private final BrandRepository brands;
    public CatalogService(CategoryRepository categories, BrandRepository brands) { this.categories = categories; this.brands = brands; }
    public List<CategoryResponse> categories() { return categories.findByActiveTrueOrderByNameAsc().stream().map(CategoryResponse::from).toList(); }
    public CategoryResponse category(Long id) { return CategoryResponse.from(categories.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found."))); }
    public List<BrandResponse> brands() { return brands.findByActiveTrueOrderByNameAsc().stream().map(BrandResponse::from).toList(); }
    public BrandResponse brand(Long id) { return BrandResponse.from(brands.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand not found."))); }
    @Transactional public CategoryResponse createCategory(CategoryRequest r) {
        if (categories.existsByNameIgnoreCase(r.name())) throw new DuplicateResourceException("Category name is already in use.");
        Category c = new Category(r.name().trim(), r.description()); c.setImageUrl(r.imageUrl()); if (r.active() != null) c.setActive(r.active());
        return CategoryResponse.from(categories.save(c));
    }
    @Transactional public CategoryResponse updateCategory(Long id, CategoryRequest r) {
        Category c = categories.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found."));
        c.setName(r.name().trim()); c.setDescription(r.description()); c.setImageUrl(r.imageUrl()); if (r.active() != null) c.setActive(r.active());
        return CategoryResponse.from(c);
    }
    @Transactional public void deleteCategory(Long id) { Category c = categories.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found.")); c.setActive(false); }
    @Transactional public BrandResponse createBrand(BrandRequest r) {
        if (brands.existsByNameIgnoreCase(r.name())) throw new DuplicateResourceException("Brand name is already in use.");
        Brand b = new Brand(r.name().trim(), r.description()); b.setLogoUrl(r.logoUrl()); if (r.active() != null) b.setActive(r.active());
        return BrandResponse.from(brands.save(b));
    }
    @Transactional public BrandResponse updateBrand(Long id, BrandRequest r) {
        Brand b = brands.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand not found."));
        b.setName(r.name().trim()); b.setDescription(r.description()); b.setLogoUrl(r.logoUrl()); if (r.active() != null) b.setActive(r.active());
        return BrandResponse.from(b);
    }
    @Transactional public void deleteBrand(Long id) { Brand b = brands.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand not found.")); b.setActive(false); }
}