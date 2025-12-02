package checkout;

public class CaliforniaTaxCheckout extends CheckoutTemplate {
    
    private static final float CA_TAX_RATE = 0.0725f;
    
    @Override
    protected float calculateTax(float subtotal) {
        return subtotal * CA_TAX_RATE;
    }
}
