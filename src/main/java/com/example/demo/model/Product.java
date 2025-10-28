package com.example.demo.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prod_num")
    private Long prodNum;

    @Column(name = "prod_name", nullable = false)
    private String prodName;

    @Column(name = "prod_type")
    private String prodType;

    @Column(name = "prod_price", nullable = false)
    private BigDecimal prodPrice;

    @Column(name = "prod_line")
    private String prodLine;
    
    //@Column(name = "prod_image_url")
    //private String prodImageUrl;

    // Constructors
    public Product() {}

    public Product(String prodName, String prodType, BigDecimal prodPrice, String prodLine) {
        this.prodName = prodName;
        this.prodType = prodType;
        this.prodPrice = prodPrice;
        this.prodLine = prodLine;
    }

    // Getters and Setters
    public Long getProdNum() { return prodNum; }
    public void setProdNum(Long prodNum) { this.prodNum = prodNum; }

    public String getProdName() { return prodName; }
    public void setProdName(String prodName) { this.prodName = prodName; }

    public String getProdType() { return prodType; }
    public void setProdType(String prodType) { this.prodType = prodType; }

    public BigDecimal getProdPrice() { return prodPrice; }
    public void setProdPrice(BigDecimal prodPrice) { this.prodPrice = prodPrice; }

    public String getProdLine() { return prodLine; }
    public void setProdLine(String prodLine) { this.prodLine = prodLine; }

    @Override
    public String toString() {
        return "Product{" +
                "prodNum=" + prodNum +
                ", prodName='" + prodName + '\'' +
                ", prodType='" + prodType + '\'' +
                ", prodPrice=" + prodPrice +
                ", prodLine='" + prodLine + '\'' +
                '}';
    }
}