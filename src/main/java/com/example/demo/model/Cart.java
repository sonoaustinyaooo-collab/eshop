package com.example.demo.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 購物車實體類別
 * 一個客戶可以有一個購物車，購物車內可以有多個購物項目
 */
@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId;

    // 多對一關聯：一個購物車屬於一個客戶
    @ManyToOne
    @JoinColumn(name = "cust_num", nullable = false)
    private Customer customer;

    // 一對多關聯：一個購物車可以有多個購物項目
    // cascade = CascadeType.ALL：當購物車被刪除時，相關的購物項目也會被刪除
    // orphanRemoval = true：當從購物車移除項目時，該項目也會從資料庫刪除
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, 
            fetch = FetchType.EAGER)
 private List<CartItem> cartItems = new ArrayList<>();

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    // Constructors
    public Cart() {
        this.createdDate = new Date();
        this.updatedDate = new Date();
    }

    public Cart(Customer customer) {
        this.customer = customer;
        this.createdDate = new Date();
        this.updatedDate = new Date();
    }

    // Getters and Setters
    public Long getCartId() { return cartId; }
    public void setCartId(Long cartId) { this.cartId = cartId; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public List<CartItem> getCartItems() { return cartItems; }
    public void setCartItems(List<CartItem> cartItems) { this.cartItems = cartItems; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    public Date getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(Date updatedDate) { this.updatedDate = updatedDate; }

    // 業務方法：新增購物項目到購物車
    public void addCartItem(CartItem cartItem) {
        cartItems.add(cartItem);
        cartItem.setCart(this);
        this.updatedDate = new Date();
    }

    // 業務方法：從購物車移除購物項目
    public void removeCartItem(CartItem cartItem) {
        cartItems.remove(cartItem);
        cartItem.setCart(null);
        this.updatedDate = new Date();
    }

    // 業務方法：計算購物車總金額
    public BigDecimal getTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    // 業務方法：計算購物車總商品數量
    public int getTotalItems() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getQuantity();
        }
        return total;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "cartId=" + cartId +
                ", customer=" + customer.getCustName() +
                ", totalItems=" + getTotalItems() +
                ", totalAmount=" + getTotalAmount() +
                '}';
    }
}