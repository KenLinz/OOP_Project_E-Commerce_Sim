package checkout;

public abstract class CheckoutTemplate {
    public final float calculateTotal(float subtotal) {
        float tax = calculateTax(subtotal);
        float total = subtotal + tax;
        return total;
    }
    
    protected abstract float calculateTax(float subtotal);
    
    public float getTaxAmount(float subtotal) {
        return calculateTax(subtotal);
    }
}
