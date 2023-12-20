package com.example.shopping.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CartProductModel implements Parcelable {
    private String productUid;
    private int productCartCount = 0;

    public String getProductUid() {
        return productUid;
    }

    public void setProductUid(String productUid) {
        this.productUid = productUid;
    }

    public int getProductCartCount() {
        return productCartCount;
    }

    public void setProductCartCount(int productCartCount) {
        this.productCartCount = productCartCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.productUid);
        dest.writeInt(this.productCartCount);
    }

    public void readFromParcel(Parcel source) {
        this.productUid = source.readString();
        this.productCartCount = source.readInt();
    }

    public CartProductModel() {
    }

    protected CartProductModel(Parcel in) {
        this.productUid = in.readString();
        this.productCartCount = in.readInt();
    }

    public static final Parcelable.Creator<CartProductModel> CREATOR = new Parcelable.Creator<CartProductModel>() {
        @Override
        public CartProductModel createFromParcel(Parcel source) {
            return new CartProductModel(source);
        }

        @Override
        public CartProductModel[] newArray(int size) {
            return new CartProductModel[size];
        }
    };
}
