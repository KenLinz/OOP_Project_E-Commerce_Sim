package com.commerce.ooad.E_Commerce.service;

import com.commerce.ooad.E_Commerce.model.PaymentMethodSQL;
import com.commerce.ooad.E_Commerce.model.UserSQL;
import com.commerce.ooad.E_Commerce.repository.PaymentMethodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentMethodServiceTest {

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @InjectMocks
    private PaymentMethodService paymentMethodService;

    private UserSQL testUser;
    private PaymentMethodSQL testPaymentMethod;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        testUser = new UserSQL("john", "pass123", "john@email.com", "CO");
        testPaymentMethod = new PaymentMethodSQL(
                testUser, "Visa", new BigDecimal("1000.00"),
                null, null, "1234567812345678", "1234", "John Doe"
        );
    }

    @Test
    public void testGetUserPaymentMethods() {
        when(paymentMethodRepository.findByUser(testUser))
                .thenReturn(Arrays.asList(testPaymentMethod));

        List<PaymentMethodSQL> methods = paymentMethodService.getUserPaymentMethods(testUser);

        assertNotNull(methods);
        assertEquals(1, methods.size());
        assertEquals("Visa", methods.get(0).getPaymentType());
    }

    @Test
    public void testAddPaymentMethod() throws PaymentMethodService.PaymentMethodException {
        when(paymentMethodRepository.findByUserAndPaymentType(testUser, "Visa"))
                .thenReturn(Optional.empty());
        when(paymentMethodRepository.save(any(PaymentMethodSQL.class)))
                .thenReturn(testPaymentMethod);

        PaymentMethodSQL result = paymentMethodService.addPaymentMethod(
                testUser, "Visa", new BigDecimal("1000.00"),
                null, null, "1234567812345678", "1234", "John Doe"
        );

        assertNotNull(result);
        verify(paymentMethodRepository).save(any(PaymentMethodSQL.class));
    }

    @Test
    public void testAddPaymentMethodDuplicate() {
        when(paymentMethodRepository.findByUserAndPaymentType(testUser, "Visa"))
                .thenReturn(Optional.of(testPaymentMethod));

        assertThrows(PaymentMethodService.PaymentMethodException.class, () -> {
            paymentMethodService.addPaymentMethod(
                    testUser, "Visa", new BigDecimal("1000.00"),
                    null, null, "1234567812345678", "1234", "John Doe"
            );
        });
    }

    @Test
    public void testDeletePaymentMethodNotFound() {
        when(paymentMethodRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PaymentMethodService.PaymentMethodException.class, () -> {
            paymentMethodService.deletePaymentMethod(testUser, 999L);
        });
    }
}