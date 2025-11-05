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
    
    // 產品圖片檔名
    @Column(name = "prod_image", length = 200)
    private String prodImage;
    
    //產品描述
    @Column(name = "prod_description", columnDefinition = "TEXT")
    private String prodDescription;

    // Constructors
    public Product() {}

    //完整參數建構子
    public Product(String prodName, String prodType, BigDecimal prodPrice, 
                   String prodLine, String prodImage, String prodDescription) {
        this.prodName = prodName;
        this.prodType = prodType;
        this.prodPrice = prodPrice;
        this.prodLine = prodLine;
        this.prodImage = prodImage;
        this.prodDescription = prodDescription;
    }

    // Getters and Setters
    public Long getProdNum() { 
    	return prodNum; 
    }
    
    public void setProdNum(Long prodNum) { 
    	this.prodNum = prodNum; 
    }

    public String getProdName() { 
    	return prodName; 
    }
    
    public void setProdName(String prodName) { 
    	this.prodName = prodName; 
    }

    public String getProdType() { 
    	return prodType; 
    }
    
    public void setProdType(String prodType) { 
    	this.prodType = prodType; 
    }

    public BigDecimal getProdPrice() { 
    	return prodPrice; 
    }
    
    public void setProdPrice(BigDecimal prodPrice) { 
    	this.prodPrice = prodPrice; 
    }

    public String getProdLine() { 
    	return prodLine; 
    }
    public void setProdLine(String prodLine) { 
    	this.prodLine = prodLine; 
    }
    
    //取得產品圖片檔名
    public String getProdImage() {
        return prodImage;
    }

    public void setProdImage(String prodImage) {
        this.prodImage = prodImage;
    }
    
    //取得產品描述
    public String getProdDescription() {
        return prodDescription;
    }

    //設定產品描述
    public void setProdDescription(String prodDescription) {
        this.prodDescription = prodDescription;
    }
    
    //取得圖片的完整 URL 路徑
    public String getImageUrl() {
        if (prodImage != null && !prodImage.isEmpty()) {
            return "/resources/images/products/" + prodImage;
        }
        return null;
    }
    
    //檢查是否有圖片
    public boolean hasImage() {
        return prodImage != null && !prodImage.isEmpty();
    }
    
   //檢查是否有產品描述
    public boolean hasDescription() {
        return prodDescription != null && !prodDescription.isEmpty();
    }
    
    //取得簡短描述（用於產品列表）
    public String getShortDescription() {
        if (prodDescription == null || prodDescription.isEmpty()) {
            return "尚無產品描述";
        }
        if (prodDescription.length() > 100) {
            return prodDescription.substring(0, 100) + "...";
        }
        return prodDescription;
    }

    @Override
    public String toString() {
        return "Product{" +
                "prodNum=" + prodNum +
                ", prodName='" + prodName + '\'' +
                ", prodType='" + prodType + '\'' +
                ", prodPrice=" + prodPrice +
                ", prodLine='" + prodLine + '\'' +
                ", prodImageUrl='" + prodImage + '\'' +
                ", hasDescription=" + hasDescription() +               
                '}';
    }
}