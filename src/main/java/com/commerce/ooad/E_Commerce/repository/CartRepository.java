package com.commerce.ooad.E_Commerce.repository;

import com.commerce.ooad.E_Commerce.model.CartSQL;
import com.commerce.ooad.E_Commerce.model.PaymentMethodSQL;
import com.commerce.ooad.E_Commerce.model.UserSQL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartSQL, Long> {
    Optional<CartSQL> findByUser(UserSQL user);
}