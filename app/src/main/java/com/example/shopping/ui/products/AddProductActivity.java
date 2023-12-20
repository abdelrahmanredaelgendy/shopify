package com.example.shopping.ui.products;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopping.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {
    private EditText editTextProductName;
    private EditText editTextProductImage, editTextProductPrice, editTextProductCount, editTextProductDesc;
    private Button buttonSave;
    private String categoryUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        categoryUid = getIntent().getStringExtra("categoryUid");

        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductDesc = findViewById(R.id.editTextProductDesc);
        editTextProductImage = findViewById(R.id.editTextProductImage);
        editTextProductPrice = findViewById(R.id.editTextProductPrice);
        editTextProductCount = findViewById(R.id.editTextProductCount);
        buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(view -> saveDataToFireStore());
    }

    private void saveDataToFireStore() {
        String productName = editTextProductName.getText().toString().trim();
        String productDesc = editTextProductDesc.getText().toString().trim();
        String productImage = editTextProductImage.getText().toString().trim();
        String productCount = editTextProductCount.getText().toString().trim();
        String productPrice = editTextProductPrice.getText().toString().trim();

        if (productName.isEmpty() || productDesc.isEmpty() || productImage.isEmpty() || productCount.isEmpty() || productPrice.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("products");
        DocumentReference documentReference = collectionReference.document();
        String generatedId = documentReference.getId();
        Map<String, Object> categoryData = new HashMap<>();
        categoryData.put("productName", productName);
        categoryData.put("productNameLowerCase", productName.toLowerCase());
        categoryData.put("productDesc", productDesc);
        categoryData.put("productCount", Integer.parseInt(productCount));
        categoryData.put("productPrice", Double.parseDouble(productPrice));
        categoryData.put("productImageUrl", productImage);
        categoryData.put("uID", generatedId);
        categoryData.put("categoryUID", categoryUid);
        // Add data to Firestore
        documentReference
                .set(categoryData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent();
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    } else {
                    }
                });
    }
}