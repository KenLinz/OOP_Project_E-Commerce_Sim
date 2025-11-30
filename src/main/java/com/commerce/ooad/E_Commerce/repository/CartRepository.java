package com.commerce.ooad.E_Commerce.repository;

import com.commerce.ooad.E_Commerce.model.CartSQL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartSQL, Long> {
    Optional<CartSQL> findByUserId(Long userId);
}