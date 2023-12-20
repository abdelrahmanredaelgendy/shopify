package com.example.shopping.model;

public class ProductModel {
    private String uID;
    private String productName;
    private String productDesc;
    private double productPrice;
    private int productCount;
    private String productImageUrl;
    private String categoryUID;
    private int productCartCount;

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getCategoryUID() {
        return categoryUID;
    }

    public void setCategoryUID(String categoryUID) {
        this.categoryUID = categoryUID;
    }

    public int getProductCartCount() {
        return productCartCount;
    }

    public void setProductCartCount(int productCartCount) {
        this.productCartCount = productCartCount;
    }
}
