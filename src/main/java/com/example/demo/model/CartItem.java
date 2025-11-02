package com.example.demo.model;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 購物車項目實體類別
 * 代表購物車中的每一個產品項目
 */
@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long cartItemId;

    // 多對一關聯：多個購物項目屬於一個購物車
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    // 多對一關聯：多個購物項目可以引用同一個產品
    @ManyToOne(fetch = FetchType.EAGER)  
    @JoinColumn(name = "prod_num", nullable = false)
    private Product product;

    // 產品數量
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    // 單價（購買時的價格，避免產品價格變動影響歷史資料）
    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    // Constructors
    public CartItem() {}

    public CartItem(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getProdPrice(); // 記錄當前價格
    }

    // Getters and Setters
    public Long getCartItemId() { return cartItemId; }
    public void setCartItemId(Long cartItemId) { this.cartItemId = cartItemId; }

    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    // 業務方法：計算小計（單價 × 數量）
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "cartItemId=" + cartItemId +
                ", product=" + product.getProdName() +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", subtotal=" + getSubtotal() +
                '}';
    }
}