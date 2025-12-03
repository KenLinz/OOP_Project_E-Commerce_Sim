package checkout;

import product.IProduct;
import java.math.BigDecimal;
import java.util.List;

public class CheckoutResult {
    private final BigDecimal subtotal;
    private final BigDecimal taxRate;
    private final BigDecimal taxAmount;
    private final BigDecimal shippingCost;
    private final BigDecimal total;
    private final String state;
    private final List<IProduct> orderItems;

    public CheckoutResult(BigDecimal subtotal, BigDecimal taxRate, BigDecimal taxAmount, BigDecimal shippingCost, BigDecimal total, String state, List<IProduct> orderItems) {
        this.subtotal = subtotal;
        this.taxRate = taxRate;
        this.taxAmount = taxAmount;
        this.shippingCost = shippingCost;
        this.total = total;
        this.state = state;
        this.orderItems = orderItems;
    }

    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getTaxRate() { return taxRate; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public BigDecimal getShippingCost() { return shippingCost; }
    public BigDecimal getTotal() { return total; }
    public String getState() { return state; }
    public List<IProduct> getOrderItems() { return orderItems; }
}
