package product;

import com.commerce.ooad.E_Commerce.model.CartItemSQL;
import com.commerce.ooad.E_Commerce.model.CartSQL;
import com.commerce.ooad.E_Commerce.model.ProductSQL;
import com.commerce.ooad.E_Commerce.model.UserSQL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ProductDecoratorTest {

    private CartItemSQL cartItem;
    private UserSQL testUser;
    private CartSQL testCart;
    private ProductSQL testProduct;

    @BeforeEach
    public void setup() {
        testUser = new UserSQL("john", "pass123", "john@email.com", "CO");
        testCart = new CartSQL(testUser);
        testProduct = new ProductSQL("Laptop", new BigDecimal("100.00"));
        cartItem = new CartItemSQL(testCart, testProduct, 1);
    }

    @Test
    public void testGiftWrapDecorator() {
        IProduct product = new Product(cartItem);
        IProduct wrapped = new GiftWrapDecorator(product);

        assertTrue(wrapped.getDescription().contains("Gift Wrapped"));
        assertEquals(new BigDecimal("105.00"), wrapped.getPrice());
    }

    @Test
    public void testWarrantyDecorator() {
        IProduct product = new Product(cartItem);
        IProduct withWarranty = new WarrantyDecorator(product, 2);

        assertTrue(withWarranty.getDescription().contains("2-Year Warranty"));
        assertEquals(new BigDecimal("120.00"), withWarranty.getPrice());
    }

    @Test
    public void testDiscountDecorator() {
        IProduct product = new Product(cartItem);
        IProduct discounted = new DiscountDecorator(product, new BigDecimal("10"));

        assertTrue(discounted.getDescription().contains("10% Discount"));
        assertEquals(new BigDecimal("90.00"), discounted.getPrice());
    }

    @Test
    public void testMultipleDecorators() {
        IProduct product = new Product(cartItem);
        product = new GiftWrapDecorator(product);
        product = new WarrantyDecorator(product, 1);
        product = new DiscountDecorator(product, new BigDecimal("20"));

        assertTrue(product.getDescription().contains("Gift Wrapped"));
        assertTrue(product.getDescription().contains("1-Year Warranty"));
        assertTrue(product.getDescription().contains("20% Discount"));
    }

    @Test
    public void testProductFactory() {
        cartItem.setHasGiftWrap(true);
        cartItem.setHasWarranty(true);
        cartItem.setWarrantyYears(2);
        cartItem.setDiscountPercentage(new BigDecimal("15"));

        IProduct product = ProductFactory.createFromCartItem(cartItem);

        assertNotNull(product);
        assertTrue(product.getDescription().contains("Gift Wrapped"));
        assertTrue(product.getDescription().contains("2-Year Warranty"));
        assertTrue(product.getDescription().contains("15% Discount"));
    }

    @Test
    public void testProductFactoryBasic() {
        IProduct product = ProductFactory.createBasicProduct(cartItem);

        assertNotNull(product);
        assertEquals("Laptop (x1)", product.getDescription());
        assertEquals(new BigDecimal("100.00"), product.getPrice());
    }

    @Test
    public void testGiftWrapCost() {
        IProduct product = new Product(cartItem);
        GiftWrapDecorator wrapped = new GiftWrapDecorator(product);

        assertEquals(new BigDecimal("5.00"), wrapped.getGiftWrapCost());
    }

    @Test
    public void testWarrantyCost() {
        IProduct product = new Product(cartItem);
        WarrantyDecorator withWarranty = new WarrantyDecorator(product, 3);

        assertEquals(new BigDecimal("30.00"), withWarranty.getWarrantyCost());
        assertEquals(3, withWarranty.getWarrantyYears());
    }

    @Test
    public void testDiscountAmount() {
        IProduct product = new Product(cartItem);
        DiscountDecorator discounted = new DiscountDecorator(product, new BigDecimal("25"));

        assertEquals(new BigDecimal("25.00"), discounted.getDiscountAmount());
        assertEquals(new BigDecimal("25"), discounted.getDiscountPercentage());
    }
}