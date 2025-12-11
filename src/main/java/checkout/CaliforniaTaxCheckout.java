package checkout;

import product.IProduct;
import com.commerce.ooad.E_Commerce.model.UserSQL;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class CaliforniaTaxCheckout extends CheckoutTemplate {
    private static final BigDecimal CA_TAX_RATE = new BigDecimal("7.25");

    public CaliforniaTaxCheckout(UserSQL user, List<IProduct> orderItems) {
        super(user, orderItems);
    }

    @Override
    protected BigDecimal calculateTaxRate() {
        return CA_TAX_RATE;
    }

    @Override
    protected String getStateName() {
        return "California";
    }

    @Override
    protected BigDecimal calculateShipping(BigDecimal subtotal) {
        if (subtotal.compareTo(new BigDecimal("75.00")) >= 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal("9.99");
    }

    @Override
    protected void beforeFinalization(BigDecimal subtotal, BigDecimal taxAmount, BigDecimal shippingCost, BigDecimal total) {
        System.out.println("Processing California checkout for user: " + user.getUsername());
    }
}
