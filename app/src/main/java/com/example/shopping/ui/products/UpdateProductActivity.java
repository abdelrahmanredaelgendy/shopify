package com.example.shopping.ui.products;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopping.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateProductActivity extends AppCompatActivity {

    EditText updateProductNameEDT, updateProductImageEDT,updateEditTextProductCount,updateEditTextProductPrice;
    Button updateBTN;

    private FirebaseFirestore db;
    private DocumentReference productDocumentRef; // Reference to the specific document in Firestore
    private String categoryUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        db = FirebaseFirestore.getInstance();

        // Retrieve the data you want to update from the Intent
        categoryUid = getIntent().getStringExtra("categoryUID");
        String catName = getIntent().getStringExtra("productName");
        String productUID = getIntent().getStringExtra("productUID");
        String productImage = getIntent().getStringExtra("productImageUrl");
        String productCount = String.valueOf(getIntent().getIntExtra("productCount", 3));
        System.out.println("product count +++"+productCount);
        String productPrice =String.valueOf(getIntent().getDoubleExtra("productPrice",1));
        // Construct the document reference using the productUID
        System.out.println("6666 " + productUID);
        productDocumentRef = db.collection("products").document(productUID);

        updateProductImageEDT = findViewById(R.id.updateEditTextProductImage);
        updateProductNameEDT = findViewById(R.id.updateEditTextProductName);
        updateEditTextProductCount=findViewById(R.id.updateEditTextProductCount);
        updateEditTextProductPrice=findViewById(R.id.updateEditTextProductPrice);
        updateBTN = findViewById(R.id.buttonUpdate);

        // Set the current values in the EditText fields
        updateProductNameEDT.setText(catName);
        updateProductImageEDT.setText(productImage);
        updateEditTextProductPrice.setText(productPrice);
        updateEditTextProductCount.setText(productCount);
        updateBTN.setOnClickListener(v -> {
            String updatedName = updateProductNameEDT.getText().toString();
            String updatedImage = updateProductImageEDT.getText().toString();
            int updatedCount =Integer.parseInt(updateEditTextProductCount.getText().toString());
            double updatedPrice = Double.parseDouble(updateEditTextProductPrice.getText().toString());
            Map<String, Object> productModel = new HashMap<>();
            productModel.put("productImageUrl", updatedImage);
            productModel.put("productName", updatedName);
            productModel.put("uID", productUID);
            productModel.put("categoryUID", categoryUid);
            productModel.put("productCount",updatedCount);
            productModel.put("productPrice",updatedPrice);
            updateProduct(productModel);
        });
    }

    private void updateProduct(Map<String, Object> productModel) {

        productDocumentRef
                .set(productModel)
                .addOnSuccessListener(aVoid -> {
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);

                    finish();
                })
                .addOnFailureListener(e -> {
                });
    }
}
