package com.shoeshop.service;
import com.shoeshop.model.Supplier;
import com.shoeshop.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;

    @Autowired
    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Поставщик не найден"));
    }

    public Supplier saveSupplier(Supplier supplier) {
        validateSupplier(supplier);
        return supplierRepository.save(supplier);
    }

    public void deleteSupplier(Long id) {
        supplierRepository.deleteById(id);
    }

    public List<Supplier> findByNameContaining(String name) {
        return supplierRepository.findByNameContainingIgnoreCase(name);
    }

    private void validateSupplier(Supplier supplier) {
        if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название поставщика не может быть пустым");
        }

        if (supplier.getPhoneNumber() != null && !supplier.getPhoneNumber().matches("\\+?\\d{10,15}")) {
            throw new IllegalArgumentException("Некорректный формат телефона");
        }
    }
}
