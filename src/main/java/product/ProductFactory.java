package product;

import com.commerce.ooad.E_Commerce.model.CartItemSQL;
import java.math.BigDecimal;

public class ProductFactory {

    public static IProduct createFromCartItem(CartItemSQL cartItem) {
        IProduct product = new Product(cartItem);

        if (cartItem.getHasWarranty() != null && cartItem.getHasWarranty() && cartItem.getWarrantyYears() > 0) {
            product = new WarrantyDecorator(product, cartItem.getWarrantyYears());
        }

        if (cartItem.getHasGiftWrap() != null && cartItem.getHasGiftWrap()) {
            product = new GiftWrapDecorator(product);
        }

        if (cartItem.getDiscountPercentage() != null && cartItem.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {
            product = new DiscountDecorator(product, cartItem.getDiscountPercentage());
        }

        return product;
    }

    public static IProduct createBasicProduct(CartItemSQL cartItem) {
        return new Product(cartItem);
    }
}
