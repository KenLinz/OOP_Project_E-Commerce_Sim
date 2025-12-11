package com.commerce.ooad.E_Commerce.service;

import product.IProduct;
import checkout.CheckoutTemplate;
import checkout.CaliforniaTaxCheckout;
import checkout.ColoradoTaxCheckout;
import checkout.CheckoutResult;
import product.GiftWrapDecorator;
import product.DiscountDecorator;
import product.WarrantyDecorator;
import com.commerce.ooad.E_Commerce.model.CartSQL;
import com.commerce.ooad.E_Commerce.model.UserSQL;
import com.commerce.ooad.E_Commerce.model.PaymentMethodSQL;
import com.commerce.ooad.E_Commerce.repository.PaymentMethodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import paymentStrategy.PaymentStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CheckoutService {

    private final CartService cartService;
    private final PaymentMethodRepository paymentMethodRepository;

    public CheckoutService(CartService cartService, PaymentMethodRepository paymentMethodRepository) {
        this.cartService = cartService;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    public CheckoutResult prepareCheckout(UserSQL user) {
        CartSQL cart = cartService.getOrCreateCart(user);

        List<IProduct> orderItems = new ArrayList<>();
        cart.getItems().forEach(cartItem -> {
            IProduct item = product.ProductFactory.createFromCartItem(cartItem);

            for (int i = 0; i < cartItem.getQuantity(); i++) {
                orderItems.add(item);
            }
        });

        CheckoutTemplate checkout = createCheckoutForState(user, orderItems);
        return checkout.processCheckout();
    }

    public CheckoutResult previewCheckout(UserSQL user) {
        CartSQL cart = cartService.getOrCreateCart(user);

        List<IProduct> orderItems = new ArrayList<>();
        cart.getItems().forEach(cartItem -> {
            IProduct item = product.ProductFactory.createFromCartItem(cartItem);

            for (int i = 0; i < cartItem.getQuantity(); i++) {
                orderItems.add(item);
            }
        });

        CheckoutTemplate checkout = createCheckoutForState(user, orderItems);
        return checkout.previewCheckout();
    }


    @Transactional
    public void completeCheckout(UserSQL user, Long paymentMethodId, CheckoutResult checkoutResult)
            throws CheckoutException {

        PaymentMethodSQL paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new CheckoutException("Payment method not found"));

        if (!paymentMethod.belongsToUser(user)) {
            throw new CheckoutException("Unauthorized payment method access");
        }

        PaymentStrategy strategy = paymentMethod.toPaymentStrategy();

        if (!strategy.validate()) {
            throw new CheckoutException("Invalid payment method");
        }

        float totalFloat = checkoutResult.getTotal().floatValue();
        if (!strategy.processPayment(totalFloat)) {
            throw new CheckoutException("Payment failed - insufficient balance");
        }

        paymentMethod.updateBalanceFromStrategy(strategy);
        paymentMethodRepository.save(paymentMethod);

        cartService.clearCart(user);
    }


    private CheckoutTemplate createCheckoutForState(UserSQL user, List<IProduct> orderItems) {
        String state = user.getState();

        switch (state.toLowerCase()) {
            case "co":
                return new ColoradoTaxCheckout(user, orderItems);
            case "ca":
                return new CaliforniaTaxCheckout(user, orderItems);
            default:
                return new ColoradoTaxCheckout(user, orderItems);
        }
    }

    public static class CheckoutException extends Exception {
        public CheckoutException(String message) {
            super(message);
        }
    }
}