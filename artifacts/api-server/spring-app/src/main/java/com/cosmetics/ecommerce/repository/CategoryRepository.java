package com.cosmetics.ecommerce.repository;

import com.cosmetics.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByActiveTrueOrderByNameAsc();
    boolean existsByNameIgnoreCase(String name);
}