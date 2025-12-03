package com.commerce.ooad.E_Commerce.service;

import com.commerce.ooad.E_Commerce.model.PaymentMethodSQL;
import com.commerce.ooad.E_Commerce.model.UserSQL;
import com.commerce.ooad.E_Commerce.repository.PaymentMethodRepository;
import paymentStrategy.PaymentStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentMethodService(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    public List<PaymentMethodSQL> getUserPaymentMethods(UserSQL user) {
        return paymentMethodRepository.findByUser(user);
    }

    @Transactional
    public PaymentMethodSQL addPaymentMethod(UserSQL user, String paymentType,
                                             BigDecimal balance, String paymentEmail,
                                             String paymentPassword, String paymentCardNumber,
                                             String paymentCardPin, String paymentCardName)
            throws PaymentMethodException {

        if (paymentMethodRepository.findByUserAndPaymentType(user, paymentType).isPresent()) {
            throw new PaymentMethodException("Already existing payment method with that type!");
        }

        PaymentMethodSQL newPaymentMethod = new PaymentMethodSQL(
                user, paymentType, balance, paymentEmail, paymentPassword,
                paymentCardNumber, paymentCardPin, paymentCardName
        );

        try {
            PaymentStrategy strategy = newPaymentMethod.toPaymentStrategy();
            if (!strategy.validate()) {
                throw new PaymentMethodException("Invalid payment details. Please check your information again");
            }
        } catch (IllegalArgumentException e) {
            throw new PaymentMethodException(e.getMessage());
        }

        return paymentMethodRepository.save(newPaymentMethod);
    }

    @Transactional
    public void deletePaymentMethod(UserSQL user, Long methodId)
            throws PaymentMethodException {
        Optional<PaymentMethodSQL> paymentMethodOptional = paymentMethodRepository.findById(methodId);

        if (paymentMethodOptional.isEmpty()) {
            throw new PaymentMethodException("Payment method not found");
        }

        PaymentMethodSQL paymentMethod = paymentMethodOptional.get();
        if (!paymentMethod.belongsToUser(user)) {
            throw new PaymentMethodException("Unauthorized access to payment method");
        }

        paymentMethodRepository.delete(paymentMethod);
    }

    public static class PaymentMethodException extends Exception {
        public PaymentMethodException(String message) {
            super(message);
        }
    }
}