package checkout;

import checkout.CheckoutTemplate;
import checkout.CaliforniaTaxCheckout;
import checkout.ColoradoTaxCheckout;
import com.commerce.ooad.E_Commerce.model.UserSQL;
import product.IProduct;

import java.util.List;

public class CheckoutFactory {

    public static CheckoutTemplate createCheckout(UserSQL user, List<IProduct> orderItems) {
        String state = user.getState();

        return switch (state.toLowerCase()) {
            case "co" -> new ColoradoTaxCheckout(user, orderItems);
            case "ca" -> new CaliforniaTaxCheckout(user, orderItems);
            default -> new ColoradoTaxCheckout(user, orderItems);
        };
    }
}