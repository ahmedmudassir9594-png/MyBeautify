package com.cosmetics.ecommerce.repository;

import com.cosmetics.ecommerce.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    List<Brand> findByActiveTrueOrderByNameAsc();
    boolean existsByNameIgnoreCase(String name);
}