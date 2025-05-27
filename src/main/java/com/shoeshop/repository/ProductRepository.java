package com.shoeshop.repository;

import com.shoeshop.model.Product;
import com.shoeshop.model.Supplier;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByStockQuantityGreaterThan(Integer quantity);
    @Query("SELECT p FROM Product p WHERE p.supplier = :supplier AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "CAST(p.price AS string) LIKE CONCAT('%', :searchText, '%'))")
    List<Product> findBySupplierAndSearchText(@Param("supplier") Supplier supplier,
                                              @Param("searchText") String searchText);
    @Query("SELECT p FROM Product p WHERE p.supplier = :supplier AND " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> findBySupplierAndNameContainingIgnoreCase(@Param("supplier") Supplier supplier,
                                                            @Param("name") String name);
    List<Product> findByStockQuantityGreaterThan(int quantity, Sort sort);

}

