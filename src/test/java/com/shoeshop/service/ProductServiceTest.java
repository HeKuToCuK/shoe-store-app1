package com.shoeshop.service;

import com.shoeshop.model.Product;
import com.shoeshop.model.Supplier;
import com.shoeshop.repository.ProductRepository;
import com.shoeshop.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    private Supplier testSupplier;

    @BeforeEach
    void setUp() {
        // Подготовка тестового поставщика
        testSupplier = new Supplier("Adidas");
        testSupplier.setCreatedAt(LocalDateTime.now());
        supplierRepository.save(testSupplier);
    }

    @Test
    public void saveProduct_Success() {
        Product product = new Product();
        product.setName("Кроссовки Ultraboost");
        product.setPrice(12999.0);
        product.setStockQuantity(15);
        product.setSupplier(testSupplier);

        Product savedProduct = productService.saveProduct(product);

        assertNotNull(savedProduct.getId());
        assertEquals("Кроссовки Ultraboost", savedProduct.getName());
        assertEquals(15, savedProduct.getStockQuantity());
        assertNotNull(savedProduct.getSupplier().getCreatedAt());
    }

    @Test
    public void findByName_NoResults() {
        List<Product> results = productService.findByNameContainingIgnoreCase("Nonexistent");

        assertTrue(results.isEmpty());
    }
}