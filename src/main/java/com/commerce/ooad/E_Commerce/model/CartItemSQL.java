package com.commerce.ooad.E_Commerce.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItemSQL {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private CartSQL cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductSQL product;


    public CartItemSQL() {}

    public CartItemSQL(CartSQL cart, ProductSQL product, Integer quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public CartSQL getCart() {
        return cart;
    }

    public void setCart(CartSQL cart) {
        this.cart = cart;
    }

    public ProductSQL getProduct() {
        return product;
    }

    public void setProduct(ProductSQL product) {
        this.product = product;
    }
}