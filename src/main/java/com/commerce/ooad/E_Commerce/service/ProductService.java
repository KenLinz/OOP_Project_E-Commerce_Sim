package com.commerce.ooad.E_Commerce.service;

import com.commerce.ooad.E_Commerce.model.ProductSQL;
import com.commerce.ooad.E_Commerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductSQL> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<ProductSQL> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<ProductSQL> getAvailableProducts() {
        return productRepository.findAll().stream()
                .filter(ProductSQL::isAvailable)
                .toList();
    }
}