package com.commerce.ooad.E_Commerce.model;

import jakarta.persistence.*;
import java.util.List;

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
    private List<CartItemSQL> items;

    public CartSQL() {}

    public CartSQL(UserSQL user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public UserSQL getUser() {
        return user;
    }

    public void setUser(UserSQL user) {
        this.user = user;
    }

    public List<CartItemSQL> getItems() {
        return items;
    }

    public void setItems(List<CartItemSQL> items) {
        this.items = items;
    }
}