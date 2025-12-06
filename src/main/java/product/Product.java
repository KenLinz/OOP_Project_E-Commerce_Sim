package product;

import com.commerce.ooad.E_Commerce.model.CartItemSQL;
import java.math.BigDecimal;

public class Product implements IProduct {
    private final CartItemSQL cartItem;

    public Product(CartItemSQL cartItem) {
        this.cartItem = cartItem;
    }

    @Override
    public String getDescription() {
        return cartItem.getProduct().getName() + " (x" + cartItem.getQuantity() + ")";
    }

    @Override
    public BigDecimal getPrice() {
        return cartItem.getProduct().getCost();
    }

    public CartItemSQL getCartItem() {
        return cartItem;
    }
}
