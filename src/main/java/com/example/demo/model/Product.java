package com.example.demo.model;

import javax.persistence.*;
import java.math.BigDecimal;
import javax.persistence.*;

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
    
    /**
     * 產品圖片 URL
     * 
     * 用途：
     * - 儲存產品圖片的網址
     * - 可以是 GitHub、Imgur、或其他圖床的連結
     * 
     * 範例：
     * https://raw.githubusercontent.com/username/repo/main/images/product1.jpg
     * 
     * 最大長度 500 字元
     */
    @Column(name = "prod_image_url", length = 500)
    private String prodImageUrl;
    
    /**
     * 產品描述
     * 
     * 用途：
     * - 顯示在商品詳細頁面
     * - 提供產品的詳細說明
     * 
     * 使用 TEXT 型別，可儲存大量文字
     */
    @Column(name = "prod_description", columnDefinition = "TEXT")
    private String prodDescription;

    // Constructors
    public Product() {}

    /**
     * 完整建構子（含圖片 URL 和描述）
     */
    public Product(String prodName, String prodType, BigDecimal prodPrice, 
                   String prodLine, String prodImageUrl, String prodDescription) {
        this.prodName = prodName;
        this.prodType = prodType;
        this.prodPrice = prodPrice;
        this.prodLine = prodLine;
        this.prodImageUrl = prodImageUrl;
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
    
    /**
     * ⭐ 設定產品圖片 URL
     * @param prodImageUrl 圖片的完整 URL
     */
    public void setProdImageUrl(String prodImageUrl) {
        this.prodImageUrl = prodImageUrl;
    }

    /**
     * ⭐ 取得產品描述
     * @return 產品描述，可能為 null
     */
    public String getProdDescription() {
        return prodDescription;
    }

    /**
     * ⭐ 設定產品描述
     * @param prodDescription 產品的詳細描述
     */
    public void setProdDescription(String prodDescription) {
        this.prodDescription = prodDescription;
    }

    @Override
    public String toString() {
        return "Product{" +
                "prodNum=" + prodNum +
                ", prodName='" + prodName + '\'' +
                ", prodType='" + prodType + '\'' +
                ", prodPrice=" + prodPrice +
                ", prodLine='" + prodLine + '\'' +
                ", prodImageUrl='" + prodImageUrl + '\'' +
                ", prodDescription='" + prodDescription + '\'' +                
                '}';
    }
}