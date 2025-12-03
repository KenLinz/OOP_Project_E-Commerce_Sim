package checkout;

import product.IProduct;
import com.commerce.ooad.E_Commerce.model.UserSQL;
import java.math.BigDecimal;
import java.util.List;

public class ColoradoTaxCheckout extends CheckoutTemplate {

    private static final BigDecimal CO_TAX_RATE = new BigDecimal("2.90");

    public ColoradoTaxCheckout(UserSQL user, List<IProduct> orderItems) {
        super(user, orderItems);
    }

    @Override
    protected BigDecimal calculateTaxRate() {
        return CO_TAX_RATE;
    }

    @Override
    protected String getStateName() {
        return "Colorado";
    }

    @Override
    protected void beforeFinalization(BigDecimal subtotal, BigDecimal taxAmount, BigDecimal shippingCost, BigDecimal total) {
        System.out.println("Processing Colorado checkout for user: " + user.getUsername());
    }
}
