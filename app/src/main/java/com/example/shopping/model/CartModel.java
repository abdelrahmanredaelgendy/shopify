package com.example.shopping.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class CartModel implements Parcelable {
    private String uid;
    private String userUid;
    private List<CartProductModel> productCartModelList;
    private int productsCount;
    private double totalPrice;
    private int isActive;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public List<CartProductModel> getProductCartModelList() {
        return productCartModelList;
    }

    public void setProductCartModelList(List<CartProductModel> productCartModelList) {
        this.productCartModelList = productCartModelList;
    }

    public int getProductsCount() {
        return productsCount;
    }

    public void setProductsCount(int productsCount) {
        this.productsCount = productsCount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.userUid);
        dest.writeTypedList(this.productCartModelList);
        dest.writeInt(this.productsCount);
        dest.writeDouble(this.totalPrice);
        dest.writeInt(this.isActive);
    }

    public void readFromParcel(Parcel source) {
        this.uid = source.readString();
        this.userUid = source.readString();
        this.productCartModelList = source.createTypedArrayList(CartProductModel.CREATOR);
        this.productsCount = source.readInt();
        this.totalPrice = source.readDouble();
        this.isActive = source.readInt();
    }

    public CartModel() {
    }

    protected CartModel(Parcel in) {
        this.uid = in.readString();
        this.userUid = in.readString();
        this.productCartModelList = in.createTypedArrayList(CartProductModel.CREATOR);
        this.productsCount = in.readInt();
        this.totalPrice = in.readDouble();
        this.isActive = in.readInt();
    }

    public static final Parcelable.Creator<CartModel> CREATOR = new Parcelable.Creator<CartModel>() {
        @Override
        public CartModel createFromParcel(Parcel source) {
            return new CartModel(source);
        }

        @Override
        public CartModel[] newArray(int size) {
            return new CartModel[size];
        }
    };
}

