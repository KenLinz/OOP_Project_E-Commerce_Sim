package com.commerce.ooad.E_Commerce.service;

import com.commerce.ooad.E_Commerce.model.CartSQL;
import com.commerce.ooad.E_Commerce.model.ProductSQL;
import com.commerce.ooad.E_Commerce.model.UserSQL;
import com.commerce.ooad.E_Commerce.repository.CartRepository;
import com.commerce.ooad.E_Commerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public CartSQL getOrCreateCart(UserSQL user) {
        Optional<CartSQL> existingCart = cartRepository.findByUser(user);
        if (existingCart.isPresent()) {
            return existingCart.get();
        }
        CartSQL newCart = new CartSQL(user);
        return cartRepository.save(newCart);
    }

    @Transactional
    public void addProductToCart(UserSQL user, Long productId, Integer quantity,
                                Boolean hasWarranty, Integer warrantyYears,
                                Boolean hasGiftWrap, BigDecimal discountPercentage)
            throws ProductNotFoundException {
        CartSQL cart = getOrCreateCart(user);
        ProductSQL product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product does not exist"));

        cart.addProduct(product, quantity, hasWarranty, warrantyYears, hasGiftWrap, discountPercentage);
        cartRepository.save(cart);
    }

    @Transactional
    public void removeItemFromCart(UserSQL user, Long itemId) throws ItemNotFoundException {
        CartSQL cart = getOrCreateCart(user);

        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(itemId));

        if (!removed) {
            throw new ItemNotFoundException("Cart item not found");
        }

        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(UserSQL user) {
        CartSQL cart = getOrCreateCart(user);
        cart.clear();
        cartRepository.save(cart);
    }

    public static class ProductNotFoundException extends Exception {
        public ProductNotFoundException(String message) {
            super(message);
        }
    }

    public static class ItemNotFoundException extends Exception {
        public ItemNotFoundException(String message) {
            super(message);
        }
    }
}