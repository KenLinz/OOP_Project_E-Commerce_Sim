package com.commerce.ooad.E_Commerce.service;

import com.commerce.ooad.E_Commerce.model.CartSQL;
import com.commerce.ooad.E_Commerce.model.ProductSQL;
import com.commerce.ooad.E_Commerce.model.UserSQL;
import com.commerce.ooad.E_Commerce.repository.CartRepository;
import com.commerce.ooad.E_Commerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    private UserSQL testUser;
    private ProductSQL testProduct;
    private CartSQL testCart;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        testUser = new UserSQL("john", "pass123", "john@email.com", "CO");
        testProduct = new ProductSQL("Laptop", new BigDecimal("999.99"));
        testCart = new CartSQL(testUser);
    }

    @Test
    public void testGetOrCreateCartExisting() {
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        CartSQL result = cartService.getOrCreateCart(testUser);

        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        verify(cartRepository, never()).save(any());
    }

    @Test
    public void testGetOrCreateCartNew() {
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(cartRepository.save(any(CartSQL.class))).thenReturn(testCart);

        CartSQL result = cartService.getOrCreateCart(testUser);

        assertNotNull(result);
        verify(cartRepository).save(any(CartSQL.class));
    }

    @Test
    public void testAddProductToCart() throws CartService.ProductNotFoundException {
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartRepository.save(testCart)).thenReturn(testCart);

        cartService.addProductToCart(testUser, 1L, 2, false, 0, false, BigDecimal.ZERO);

        verify(cartRepository).save(testCart);
    }

    @Test
    public void testAddProductToCartProductNotFound() {
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CartService.ProductNotFoundException.class, () -> {
            cartService.addProductToCart(testUser, 999L, 2, false, 0, false, BigDecimal.ZERO);
        });
    }

    @Test
    public void testRemoveItemFromCart() throws CartService.ItemNotFoundException {
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        assertThrows(CartService.ItemNotFoundException.class, () -> {
            cartService.removeItemFromCart(testUser, 999L);
        });
    }

    @Test
    public void testClearCart() {
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        testCart.addProduct(testProduct, 2, false, 0, false, BigDecimal.ZERO);
        when(cartRepository.save(testCart)).thenReturn(testCart);

        cartService.clearCart(testUser);

        assertTrue(testCart.isEmpty());
        verify(cartRepository).save(testCart);
    }
}