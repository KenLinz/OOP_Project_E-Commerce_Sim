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

    public void addProduct(ProductSQL product, Integer quantity) {
        Optional<CartItemSQL> existingItem = findItemByProduct(product);
        if (existingItem.isPresent()) {
            existingItem.get().increaseQuantity(quantity);
        } else {
            CartItemSQL newItem = new CartItemSQL(this, product, quantity);
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