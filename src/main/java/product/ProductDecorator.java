package product;

import java.math.BigDecimal;

public abstract class ProductDecorator implements IProduct {
    protected IProduct wrappedItem;

    public ProductDecorator(IProduct wrappedItem) {
        this.wrappedItem = wrappedItem;
    }

    @Override
    public String getDescription() {
        return wrappedItem.getDescription();
    }

    @Override
    public BigDecimal getPrice() {
        return wrappedItem.getPrice();
    }
    
}
