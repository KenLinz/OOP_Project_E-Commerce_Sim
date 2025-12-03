package com.commerce.ooad.E_Commerce.repository;

import com.commerce.ooad.E_Commerce.model.UserSQL;
import com.commerce.ooad.E_Commerce.model.PaymentMethodSQL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethodSQL, Long> {
    List<PaymentMethodSQL> findByUser(UserSQL user);
    Optional<PaymentMethodSQL> findByUserAndPaymentType(UserSQL user, String paymentType);
}
