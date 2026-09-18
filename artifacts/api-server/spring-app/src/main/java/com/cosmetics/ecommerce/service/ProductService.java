package com.cosmetics.ecommerce.service;

import com.cosmetics.ecommerce.dto.*;
import com.cosmetics.ecommerce.dto.CatalogDtos.*;
import com.cosmetics.ecommerce.entity.*;
import com.cosmetics.ecommerce.exception.*;
import com.cosmetics.ecommerce.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.Locale;

@Service
public class ProductService {
    private final ProductRepository products;
    private final CategoryRepository categories;
    private final BrandRepository brands;

    public ProductService(ProductRepository products, CategoryRepository categories, BrandRepository brands) {
        this.products = products; this.categories = categories; this.brands = brands;
    }

    public PageResponse<ProductResponse> list(String query, Long categoryId, Long brandId, Boolean featured,
                                               java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice, Pageable pageable) {
        Page<Product> page;
        if (query != null && !query.isBlank()) page = products.search(query.trim(), pageable);
        else if (categoryId != null) page = products.findByCategoryIdAndActiveTrue(categoryId, pageable);
        else if (brandId != null) page = products.findByBrandIdAndActiveTrue(brandId, pageable);
        else if (Boolean.TRUE.equals(featured)) page = products.findByFeaturedTrueAndActiveTrue(pageable);
        else if (minPrice != null && maxPrice != null) page = products.findByPriceBetween(minPrice, maxPrice, pageable);
        else page = products.findByActiveTrue(pageable);
        return PageResponse.from(page.map(this::response));
    }

    public ProductResponse get(Long id) { return response(products.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found."))); }
    public ProductResponse getBySlug(String slug) { return response(products.findBySlug(slug).orElseThrow(() -> new ResourceNotFoundException("Product not found."))); }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (products.findBySku(request.sku()).isPresent()) throw new DuplicateResourceException("SKU is already in use.");
        Brand brand = brands.findById(request.brandId()).orElseThrow(() -> new ResourceNotFoundException("Brand not found."));
        Category category = categories.findById(request.categoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found."));
        Product p = new Product(request.name().trim(), uniqueSlug(request.name()), request.sku().trim(), request.price(),
                request.stockQuantity(), brand, category);
        copy(request, p); return response(products.save(p));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product p = products.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found."));
        products.findBySku(request.sku()).filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> { throw new DuplicateResourceException("SKU is already in use."); });
        p.setName(request.name().trim()); p.setSku(request.sku().trim()); p.setPrice(request.price());
        p.setStockQuantity(request.stockQuantity());
        p.setBrand(brands.findById(request.brandId()).orElseThrow(() -> new ResourceNotFoundException("Brand not found.")));
        p.setCategory(categories.findById(request.categoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found.")));
        copy(request, p); return response(p);
    }

    @Transactional
    public void delete(Long id) {
        Product p = products.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found."));
        p.setActive(false);
    }

    @Transactional
    public ProductResponse updateStock(Long id, int stock) {
        if (stock < 0) throw new BadRequestException("Stock cannot be negative.");
        Product p = products.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found."));
        p.setStockQuantity(stock); return response(p);
    }

    private void copy(ProductRequest r, Product p) {
        p.setDiscountPrice(r.discountPrice()); p.setLowStockThreshold(r.lowStockThreshold()); p.setDescription(r.description());
        p.setShortDescription(r.shortDescription()); p.setImageUrl(r.imageUrl()); p.setActive(r.active() == null || r.active());
        p.setFeatured(Boolean.TRUE.equals(r.featured())); p.setIngredients(r.ingredients()); p.setBenefits(r.benefits());
        p.setHowToUse(r.howToUse()); p.setSkinType(r.skinType()); p.setShade(r.shade()); p.setSize(r.size()); p.setExpiryDate(r.expiryDate());
    }

    public ProductResponse response(Product p) {
        Double rating = products.averageRating(p.getId());
        return ProductResponse.from(p, rating == null ? 0d : rating);
    }

    public java.util.List<ProductResponse> inventory(boolean outOfStock) {
        return products.findAll().stream()
                .filter(p -> outOfStock ? p.getStockQuantity() == 0 : p.getStockQuantity() <= p.getLowStockThreshold())
                .map(this::response).toList();
    }

    public java.util.List<ProductResponse> allInventory() {
        return products.findAll().stream().map(this::response).toList();
    }

    private String uniqueSlug(String input) {
        String base = Normalizer.normalize(input.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        String slug = base; int suffix = 2;
        while (products.findBySlug(slug).isPresent()) slug = base + "-" + suffix++;
        return slug;
    }
}