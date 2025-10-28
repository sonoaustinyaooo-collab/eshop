package com.example.demo.model;

import javax.persistence.*;

/**
 * 客戶實體類別
 * 用於顧客登入和購物
 */
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cust_num")
    private Long custNum;

    @Column(name = "cust_username", unique = true, nullable = false)
    private String custUsername;  // 客戶登入帳號

    @Column(name = "cust_password", nullable = false)
    private String custPassword;  // 客戶登入密碼

    @Column(name = "cust_name", nullable = false)
    private String custName;

    @Column(name = "cust_email", unique = true, nullable = false)
    private String custEmail;

    @Column(name = "cust_phone")
    private String custPhone;

    @Column(name = "cust_address")
    private String custAddress;

    // Constructors
    public Customer() {}

    public Customer(String custUsername, String custPassword, String custName, String custEmail, String custPhone, String custAddress) {
        this.custUsername = custUsername;
        this.custPassword = custPassword;
        this.custName = custName;
        this.custEmail = custEmail;
        this.custPhone = custPhone;
        this.custAddress = custAddress;
    }

    // Getters and Setters
    public Long getCustNum() { return custNum; }
    public void setCustNum(Long custNum) { this.custNum = custNum; }

    public String getCustUsername() { return custUsername; }
    public void setCustUsername(String custUsername) { this.custUsername = custUsername; }

    public String getCustPassword() { return custPassword; }
    public void setCustPassword(String custPassword) { this.custPassword = custPassword; }

    public String getCustName() { return custName; }
    public void setCustName(String custName) { this.custName = custName; }

    public String getCustEmail() { return custEmail; }
    public void setCustEmail(String custEmail) { this.custEmail = custEmail; }

    public String getCustPhone() { return custPhone; }
    public void setCustPhone(String custPhone) { this.custPhone = custPhone; }

    public String getCustAddress() { return custAddress; }
    public void setCustAddress(String custAddress) { this.custAddress = custAddress; }

    @Override
    public String toString() {
        return "Customer{custNum=" + custNum + ", custName='" + custName + "', custEmail='" + custEmail + "'}";
    }
}