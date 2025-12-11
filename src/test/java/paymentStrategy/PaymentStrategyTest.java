package paymentStrategy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentStrategyTest {

    @Test
    public void testVisaValidationSuccess() {
        VisaStrategy visa = new VisaStrategy("1234567812345678", "1234", "John Doe", 1000.0f);

        assertTrue(visa.validate());
    }

    @Test
    public void testVisaProcessPaymentSuccess() {
        VisaStrategy visa = new VisaStrategy("1234567812345678", "1234", "John Doe", 1000.0f);

        assertTrue(visa.processPayment(100.0f));
        assertEquals(900.0f, visa.getBalance());
    }

    @Test
    public void testVisaProcessPaymentInsufficientBalance() {
        VisaStrategy visa = new VisaStrategy("1234567812345678", "1234", "John Doe", 50.0f);

        assertFalse(visa.processPayment(100.0f));
        assertEquals(50.0f, visa.getBalance());
    }

    @Test
    public void testPaypalValidationSuccess() {
        PaypalStrategy paypal = new PaypalStrategy("test@email.com", "password", 500.0f);

        assertTrue(paypal.validate());
    }

    @Test
    public void testPaypalProcessPaymentSuccess() {
        PaypalStrategy paypal = new PaypalStrategy("test@email.com", "password", 500.0f);

        assertTrue(paypal.processPayment(200.0f));
        assertEquals(300.0f, paypal.getBalance());
    }

    @Test
    public void testPaypalProcessPaymentInsufficientBalance() {
        PaypalStrategy paypal = new PaypalStrategy("test@email.com", "password", 100.0f);

        assertFalse(paypal.processPayment(200.0f));
        assertEquals(100.0f, paypal.getBalance());
    }

    @Test
    public void testVisaGetPaymentType() {
        VisaStrategy visa = new VisaStrategy("1234567812345678", "1234", "John Doe", 1000.0f);

        assertEquals("Visa", visa.getPaymentType());
    }

    @Test
    public void testPaypalGetPaymentType() {
        PaypalStrategy paypal = new PaypalStrategy("test@email.com", "password", 500.0f);

        assertEquals("PayPal", paypal.getPaymentType());
    }
}