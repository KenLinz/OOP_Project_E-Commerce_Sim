package checkout;

public class ColoradoTaxCheckout extends CheckoutTemplate {
    
    private static final float CO_TAX_RATE = 0.029f;  // 2.9% tax
    
    @Override
    protected float calculateTax(float subtotal) {
        return subtotal * CO_TAX_RATE;
    }
}
