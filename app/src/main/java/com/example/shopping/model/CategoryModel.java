package com.example.shopping.model;

import java.util.ArrayList;

public class CategoryModel extends ArrayList<CharSequence> {
    private String uID; // Update this field name to match your actual field name in Firestore
    private String catName;
    private String catImageUrl;

    // Required empty constructor for Firestore
    public CategoryModel() {
    }

    public CategoryModel(String uID, String catName, String catImageUrl) {
        this.uID = uID;
        this.catName = catName;
        this.catImageUrl = catImageUrl;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getCatImageUrl() {
        return catImageUrl;
    }

    public void setCatImageUrl(String catImageUrl) {
        this.catImageUrl = catImageUrl;
    }
}
