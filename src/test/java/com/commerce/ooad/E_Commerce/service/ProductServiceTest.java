package com.commerce.ooad.E_Commerce.service;

import com.commerce.ooad.E_Commerce.model.ProductSQL;
import com.commerce.ooad.E_Commerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;
    private ProductSQL product1;
    private ProductSQL product2;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        product1 = new ProductSQL("Laptop", new BigDecimal("999.99"));
        product2 = new ProductSQL("Mouse", new BigDecimal("29.99"));
    }

    @Test
    public void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));
        List<ProductSQL> result = productService.getAllProducts();
        assertEquals(2, result.size());
        verify(productRepository).findAll();
    }

    @Test
    public void testGetProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        Optional<ProductSQL> result = productService.getProductById(1L);
        assertTrue(result.isPresent());
        assertEquals("Laptop", result.get().getName());
    }

    @Test
    public void testGetProductByIdNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<ProductSQL> result = productService.getProductById(999L);
        assertFalse(result.isPresent());
    }
}