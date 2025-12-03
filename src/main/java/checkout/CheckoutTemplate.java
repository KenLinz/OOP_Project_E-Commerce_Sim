package checkout;

import product.IProduct;
import com.commerce.ooad.E_Commerce.model.UserSQL;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public abstract class CheckoutTemplate {
    protected UserSQL user;
    protected List<IProduct> orderItems;

    public CheckoutTemplate(UserSQL user, List<IProduct> orderItems) {
        this.user = user;
        this.orderItems = orderItems;
    }

    public final CheckoutResult previewCheckout() {
        BigDecimal subtotal = calculateSubtotal();
        BigDecimal taxRate = calculateTaxRate();
        BigDecimal taxAmount = calculateTax(subtotal, taxRate);
        BigDecimal shippingCost = calculateShipping(subtotal);
        BigDecimal total = subtotal.add(taxAmount).add(shippingCost);

        return new CheckoutResult(
                subtotal,
                taxRate,
                taxAmount,
                shippingCost,
                total,
                getStateName(),
                orderItems
        );
    }

    public final CheckoutResult processCheckout() {
        BigDecimal subtotal = calculateSubtotal();
        BigDecimal taxRate = calculateTaxRate();
        BigDecimal taxAmount = calculateTax(subtotal, taxRate);
        BigDecimal shippingCost = calculateShipping(subtotal);
        BigDecimal total = subtotal.add(taxAmount).add(shippingCost);

        beforeFinalization(subtotal, taxAmount, shippingCost, total);

        return new CheckoutResult(
                subtotal,
                taxRate,
                taxAmount,
                shippingCost,
                total,
                getStateName(),
                orderItems
        );
    }

    protected BigDecimal calculateSubtotal() {
        return orderItems.stream()
                .map(IProduct::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    protected abstract BigDecimal calculateTaxRate();

    protected abstract String getStateName();

    protected BigDecimal calculateTax(BigDecimal subtotal, BigDecimal taxRate) {
        return subtotal.multiply(taxRate)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    protected BigDecimal calculateShipping(BigDecimal subtotal) {
        if (subtotal.compareTo(new BigDecimal("50.00")) >= 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal("7.99");
    }

    protected void beforeFinalization(BigDecimal subtotal, BigDecimal taxAmount, BigDecimal shippingCost, BigDecimal total) {
    }
}