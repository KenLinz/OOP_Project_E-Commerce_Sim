package product;

import java.math.BigDecimal;

public class WarrantyDecorator extends ProductDecorator {
    private final int warrantyYears;
    private static final BigDecimal WARRANTY_COST_PER_YEAR = new BigDecimal("10.00");

    public WarrantyDecorator(IProduct wrappedItem, int warrantyYears) {
        super(wrappedItem);
        this.warrantyYears = warrantyYears;
    }

    @Override
    public String getDescription() {
        return wrappedItem.getDescription() + " [" + warrantyYears + "-Year Warranty]";
    }

    @Override
    public BigDecimal getPrice() {
        BigDecimal warrantyCost = WARRANTY_COST_PER_YEAR.multiply(new BigDecimal(warrantyYears));
        return wrappedItem.getPrice().add(warrantyCost);
    }

    public int getWarrantyYears() {
        return warrantyYears;
    }

    public BigDecimal getWarrantyCost() {
        return WARRANTY_COST_PER_YEAR.multiply(new BigDecimal(warrantyYears));
    }
}
