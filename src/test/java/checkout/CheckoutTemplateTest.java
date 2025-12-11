package checkout;

import com.commerce.ooad.E_Commerce.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import product.IProduct;
import product.Product;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CheckoutTemplateTest {
    private UserSQL coloradoUser;
    private UserSQL californiaUser;
    private List<IProduct> orderItems;

    @BeforeEach
    public void setup() {
        coloradoUser = new UserSQL("john", "pass123", "john@email.com", "CO");
        californiaUser = new UserSQL("jane", "pass456", "jane@email.com", "CA");
        CartSQL cart = new CartSQL(coloradoUser);
        ProductSQL product = new ProductSQL("Laptop", new BigDecimal("100.00"));
        CartItemSQL cartItem = new CartItemSQL(cart, product, 1);
        orderItems = new ArrayList<>();
        orderItems.add(new Product(cartItem));
        orderItems.add(new Product(cartItem));
    }

    @Test
    public void testColoradoTaxCheckout() {
        CheckoutTemplate checkout = new ColoradoTaxCheckout(coloradoUser, orderItems);
        CheckoutResult result = checkout.processCheckout();
        assertEquals("Colorado", result.getState());
        assertEquals(new BigDecimal("2.90"), result.getTaxRate());
        assertEquals(new BigDecimal("200.00"), result.getSubtotal());
    }

    @Test
    public void testCaliforniaTaxCheckout() {
        CheckoutTemplate checkout = new CaliforniaTaxCheckout(californiaUser, orderItems);
        CheckoutResult result = checkout.processCheckout();
        assertEquals("California", result.getState());
        assertEquals(new BigDecimal("7.25"), result.getTaxRate());
        assertEquals(new BigDecimal("200.00"), result.getSubtotal());
    }

    @Test
    public void testColoradoShippingUnderThreshold() {
        CartSQL cart = new CartSQL(coloradoUser);
        ProductSQL product = new ProductSQL("Mouse", new BigDecimal("20.00"));
        CartItemSQL cartItem = new CartItemSQL(cart, product, 1);
        List<IProduct> smallOrder = new ArrayList<>();
        smallOrder.add(new Product(cartItem));
        CheckoutTemplate checkout = new ColoradoTaxCheckout(coloradoUser, smallOrder);
        CheckoutResult result = checkout.processCheckout();
        assertEquals(new BigDecimal("7.99"), result.getShippingCost());
    }

    @Test
    public void testColoradoShippingOverThreshold() {
        CheckoutTemplate checkout = new ColoradoTaxCheckout(coloradoUser, orderItems);
        CheckoutResult result = checkout.processCheckout();
        assertEquals(BigDecimal.ZERO, result.getShippingCost());
    }

    @Test
    public void testCaliforniaShippingUnderThreshold() {
        CartSQL cart = new CartSQL(californiaUser);
        ProductSQL product = new ProductSQL("Mouse", new BigDecimal("30.00"));
        CartItemSQL cartItem = new CartItemSQL(cart, product, 1);
        List<IProduct> smallOrder = new ArrayList<>();
        smallOrder.add(new Product(cartItem));
        CheckoutTemplate checkout = new CaliforniaTaxCheckout(californiaUser, smallOrder);
        CheckoutResult result = checkout.processCheckout();
        assertEquals(new BigDecimal("9.99"), result.getShippingCost());
    }

    @Test
    public void testCaliforniaShippingOverThreshold() {
        CartSQL cart = new CartSQL(californiaUser);
        ProductSQL product = new ProductSQL("Expensive Item", new BigDecimal("100.00"));
        CartItemSQL cartItem = new CartItemSQL(cart, product, 1);
        List<IProduct> largeOrder = new ArrayList<>();
        largeOrder.add(new Product(cartItem));
        CheckoutTemplate checkout = new CaliforniaTaxCheckout(californiaUser, largeOrder);
        CheckoutResult result = checkout.processCheckout();
        assertEquals(BigDecimal.ZERO, result.getShippingCost());
    }

    @Test
    public void testPreviewCheckout() {
        CheckoutTemplate checkout = new ColoradoTaxCheckout(coloradoUser, orderItems);
        CheckoutResult result = checkout.previewCheckout();
        assertNotNull(result);
        assertEquals(new BigDecimal("200.00"), result.getSubtotal());
        assertTrue(result.getTotal().compareTo(result.getSubtotal()) > 0);
    }

    @Test
    public void testTaxCalculation() {
        CheckoutTemplate checkout = new ColoradoTaxCheckout(coloradoUser, orderItems);
        CheckoutResult result = checkout.processCheckout();
        BigDecimal expectedTax = new BigDecimal("5.80");
        assertEquals(expectedTax, result.getTaxAmount());
    }

    @Test
    public void testTotalCalculation() {
        CheckoutTemplate checkout = new ColoradoTaxCheckout(coloradoUser, orderItems);
        CheckoutResult result = checkout.processCheckout();
        BigDecimal expectedTotal = result.getSubtotal().add(result.getTaxAmount()).add(result.getShippingCost());
        assertEquals(expectedTotal, result.getTotal());
    }

    @Test
    public void testOrderItemsInResult() {
        CheckoutTemplate checkout = new ColoradoTaxCheckout(coloradoUser, orderItems);
        CheckoutResult result = checkout.processCheckout();
        assertEquals(orderItems.size(), result.getOrderItems().size());
    }
}