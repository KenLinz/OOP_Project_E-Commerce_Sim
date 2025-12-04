package com.commerce.ooad.E_Commerce.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "carts")
public class CartSQL {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true, nullable = false)
    private UserSQL user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemSQL> items = new ArrayList<>();

    public CartSQL() {}

    public CartSQL(UserSQL user) {
        this.user = user;
        this.items = new ArrayList<>();
    }

    public void addProduct(ProductSQL product, Integer quantity,
                          Boolean hasWarranty, Integer warrantyYears,
                          Boolean hasGiftWrap, BigDecimal discountPercentage) {
        // Normalize null values to defaults for comparison
        Boolean normalizedWarranty = hasWarranty != null ? hasWarranty : false;
        Integer normalizedWarrantyYears = warrantyYears != null ? warrantyYears : 0;
        Boolean normalizedGiftWrap = hasGiftWrap != null ? hasGiftWrap : false;
        BigDecimal normalizedDiscount = discountPercentage != null ? discountPercentage : BigDecimal.ZERO;

        // Find existing item with same product AND same decorator configuration
        Optional<CartItemSQL> existingItem = findItemByProductAndDecorators(
            product, normalizedWarranty, normalizedWarrantyYears, normalizedGiftWrap, normalizedDiscount
        );

        if (existingItem.isPresent()) {
            existingItem.get().increaseQuantity(quantity);
        } else {
            CartItemSQL newItem = new CartItemSQL(this, product, quantity);
            newItem.setHasWarranty(normalizedWarranty);
            newItem.setWarrantyYears(normalizedWarrantyYears);
            newItem.setHasGiftWrap(normalizedGiftWrap);
            newItem.setDiscountPercentage(normalizedDiscount);
            items.add(newItem);
        }
    }

    public void removeProduct(ProductSQL product) {
        items.removeIf(item -> item.getProduct().getId().equals(product.getId()));
    }

    public BigDecimal calculateTotal() {
        return items.stream()
                .map(CartItemSQL::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
    }

    private Optional<CartItemSQL> findItemByProduct(ProductSQL product) {
        return items.stream()
                .filter(item -> item.belongsToProduct(product))
                .findFirst();
    }

    private Optional<CartItemSQL> findItemByProductAndDecorators(ProductSQL product,
                                                                 Boolean hasWarranty,
                                                                 Integer warrantyYears,
                                                                 Boolean hasGiftWrap,
                                                                 BigDecimal discountPercentage) {
        return items.stream()
                .filter(item -> {
                    if (!item.belongsToProduct(product)) {
                        return false;
                    }

                    // Normalize item's values for comparison
                    Boolean itemWarranty = item.getHasWarranty() != null ? item.getHasWarranty() : false;
                    Integer itemWarrantyYears = item.getWarrantyYears() != null ? item.getWarrantyYears() : 0;
                    Boolean itemGiftWrap = item.getHasGiftWrap() != null ? item.getHasGiftWrap() : false;
                    BigDecimal itemDiscount = item.getDiscountPercentage() != null ? item.getDiscountPercentage() : BigDecimal.ZERO;

                    return itemWarranty.equals(hasWarranty) &&
                           itemWarrantyYears.equals(warrantyYears) &&
                           itemGiftWrap.equals(hasGiftWrap) &&
                           itemDiscount.compareTo(discountPercentage) == 0;
                })
                .findFirst();
    }

    public Long getId() {
        return id;
    }

    public UserSQL getUser() {
        return user;
    }

    public List<CartItemSQL> getItems() {
        return new ArrayList<>(items);
    }
}