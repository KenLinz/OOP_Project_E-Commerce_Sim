package com.commerce.ooad.E_Commerce.repository;

import com.commerce.ooad.E_Commerce.model.CartItemSQL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemSQL, Long> {
    Optional<CartItemSQL> findByCartIdAndProductId(Long cartId, Long productId);
    List<CartItemSQL> findByCartId(Long cartId);
}