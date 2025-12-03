package product;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DiscountDecorator extends ProductDecorator {
    private final BigDecimal discountPercentage;

    public DiscountDecorator(IProduct wrappedItem, BigDecimal discountPercentage) {
        super(wrappedItem);
        this.discountPercentage = discountPercentage;
    }

    @Override
    public String getDescription() {
        return wrappedItem.getDescription() + " [" + discountPercentage + "% Discount]";
    }

    @Override
    public BigDecimal getPrice() {
        BigDecimal originalPrice = wrappedItem.getPrice();
        BigDecimal discountAmount = originalPrice.multiply(discountPercentage)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        return originalPrice.subtract(discountAmount);
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public BigDecimal getDiscountAmount() {
        BigDecimal originalPrice = wrappedItem.getPrice();
        return originalPrice.multiply(discountPercentage)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }
}
