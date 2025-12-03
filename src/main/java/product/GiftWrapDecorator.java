package product;

import java.math.BigDecimal;

public class GiftWrapDecorator extends ProductDecorator {
    private static final BigDecimal GIFT_WRAP_COST = new BigDecimal("5.00");

    public GiftWrapDecorator(IProduct wrappedItem) {
        super(wrappedItem);
    }

    @Override
    public String getDescription() {
        return wrappedItem.getDescription() + " [Gift Wrapped]";
    }

    @Override
    public BigDecimal getPrice() {
        return wrappedItem.getPrice().add(GIFT_WRAP_COST);
    }

    public BigDecimal getGiftWrapCost() {
        return GIFT_WRAP_COST;
    }
}
