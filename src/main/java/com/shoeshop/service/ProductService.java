package com.shoeshop.service;


import com.shoeshop.model.Product;
import com.shoeshop.model.Supplier;
import com.shoeshop.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> getAllAvailableProducts() {
        return productRepository.findByStockQuantityGreaterThan(0);
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public void updateStock(Long productId, Integer quantityChange) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStockQuantity(product.getStockQuantity() + quantityChange);
        productRepository.save(product);
    }
    public List<Product> findByNameContainingIgnoreCase(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
    public List<Product> getAll() {
        return productRepository.findAll();

    }

    public List<Product> findBySupplierAndNameContainingIgnoreCase(Supplier supplier, String name) {
        return productRepository.findBySupplierAndNameContainingIgnoreCase(supplier, name);
    }
    public List<Product> getFilteredProducts(String text, Supplier value) {
        return List.of();
    }

    @Transactional
    public Product saveProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название товара не может быть пустым");
        }
        if (product.getPrice() <= 0) {
            throw new IllegalArgumentException("Цена должна быть положительной");
        }
        if (product.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Количество не может быть отрицательным");
        }
        if (product.getSupplier() == null) {
            throw new IllegalArgumentException("Не выбран поставщик");
        }

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public Product updateProduct(Product product) {
        if (product.getId() == null) {
            throw new IllegalArgumentException("Product ID cannot be null for update");
        }

        Product existing = productRepository.findById(product.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existing.setName(product.getName());
        existing.setPrice(product.getPrice());
        existing.setStockQuantity(product.getStockQuantity());
        existing.setSupplier(product.getSupplier());

        return productRepository.save(existing);
    }
}