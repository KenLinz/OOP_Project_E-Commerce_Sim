package com.commerce.ooad.E_Commerce.service;

import checkout.CheckoutResult;
import com.commerce.ooad.E_Commerce.model.*;
import com.commerce.ooad.E_Commerce.repository.PaymentMethodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CheckoutServiceTest {

    @Mock
    private CartService cartService;

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @InjectMocks
    private CheckoutService checkoutService;

    private UserSQL testUser;
    private CartSQL testCart;
    private ProductSQL testProduct;
    private PaymentMethodSQL testPaymentMethod;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testUser = new UserSQL("john", "pass123", "john@email.com", "CO");
        setId(testUser, 1L);

        testCart = new CartSQL(testUser);
        testProduct = new ProductSQL("Laptop", new BigDecimal("100.00"));
        testCart.addProduct(testProduct, 2, false, 0, false, BigDecimal.ZERO);

        testPaymentMethod = new PaymentMethodSQL(
                testUser, "Visa", new BigDecimal("1000.00"),
                null, null, "1234567812345678", "1234", "John Doe"
        );
    }

    private void setId(UserSQL user, Long id) {
        try {
            var field = UserSQL.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (Exception e) {
        }
    }

    @Test
    public void testPrepareCheckout() {
        when(cartService.getOrCreateCart(testUser)).thenReturn(testCart);

        CheckoutResult result = checkoutService.prepareCheckout(testUser);

        assertNotNull(result);
        assertEquals(new BigDecimal("200.00"), result.getSubtotal());
    }

    @Test
    public void testPreviewCheckout() {
        when(cartService.getOrCreateCart(testUser)).thenReturn(testCart);

        CheckoutResult result = checkoutService.previewCheckout(testUser);

        assertNotNull(result);
        assertTrue(result.getTotal().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    public void testCompleteCheckoutSuccess() throws CheckoutService.CheckoutException {
        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(testPaymentMethod));
        when(paymentMethodRepository.save(testPaymentMethod)).thenReturn(testPaymentMethod);
        when(cartService.getOrCreateCart(testUser)).thenReturn(testCart);

        CheckoutResult checkoutResult = checkoutService.prepareCheckout(testUser);

        checkoutService.completeCheckout(testUser, 1L, checkoutResult);

        verify(paymentMethodRepository).save(testPaymentMethod);
        verify(cartService).clearCart(testUser);
    }

    @Test
    public void testCompleteCheckoutPaymentMethodNotFound() {
        when(paymentMethodRepository.findById(999L)).thenReturn(Optional.empty());
        when(cartService.getOrCreateCart(testUser)).thenReturn(testCart);

        CheckoutResult checkoutResult = checkoutService.prepareCheckout(testUser);

        assertThrows(CheckoutService.CheckoutException.class, () -> {
            checkoutService.completeCheckout(testUser, 999L, checkoutResult);
        });
    }

    @Test
    public void testCompleteCheckoutUnauthorized() {
        UserSQL otherUser = new UserSQL("jane", "pass456", "jane@email.com", "CA");
        setId(otherUser, 2L);

        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(testPaymentMethod));
        when(cartService.getOrCreateCart(testUser)).thenReturn(testCart);

        CheckoutResult checkoutResult = checkoutService.prepareCheckout(testUser);

        assertThrows(CheckoutService.CheckoutException.class, () -> {
            checkoutService.completeCheckout(otherUser, 1L, checkoutResult);
        });
    }

    @Test
    public void testCompleteCheckoutInsufficientBalance() {
        PaymentMethodSQL poorPayment = new PaymentMethodSQL(
                testUser, "Visa", new BigDecimal("1.00"),
                null, null, "1234567812345678", "1234", "John Doe"
        );
        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(poorPayment));
        when(cartService.getOrCreateCart(testUser)).thenReturn(testCart);

        CheckoutResult checkoutResult = checkoutService.prepareCheckout(testUser);

        assertThrows(CheckoutService.CheckoutException.class, () -> {
            checkoutService.completeCheckout(testUser, 1L, checkoutResult);
        });
    }

    @Test
    public void testPrepareCheckoutWithDecorators() {
        CartSQL decoratedCart = new CartSQL(testUser);
        ProductSQL product = new ProductSQL("Laptop", new BigDecimal("100.00"));
        decoratedCart.addProduct(product, 2, true, 2, true, new BigDecimal("10.00"));

        when(cartService.getOrCreateCart(testUser)).thenReturn(decoratedCart);

        CheckoutResult result = checkoutService.prepareCheckout(testUser);

        assertNotNull(result);
    }

    @Test
    public void testCaliforniaCheckout() {
        testUser.setState("CA");
        when(cartService.getOrCreateCart(testUser)).thenReturn(testCart);

        CheckoutResult result = checkoutService.prepareCheckout(testUser);

        assertEquals("California", result.getState());
    }

    @Test
    public void testColoradoCheckout() {
        when(cartService.getOrCreateCart(testUser)).thenReturn(testCart);

        CheckoutResult result = checkoutService.prepareCheckout(testUser);

        assertEquals("Colorado", result.getState());
    }
}