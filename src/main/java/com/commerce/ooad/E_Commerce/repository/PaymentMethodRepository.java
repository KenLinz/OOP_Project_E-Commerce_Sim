package com.commerce.ooad.E_Commerce.repository;

import com.commerce.ooad.E_Commerce.model.UserSQL;
import com.commerce.ooad.E_Commerce.model.PaymentMethodSQL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import paymentmethod.PaymentMethod;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethodSQL, Long> {
    List<PaymentMethodSQL> findByUser(UserSQL user);
}
