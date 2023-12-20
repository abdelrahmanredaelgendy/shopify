package com.example.shopping.ui.categories;

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

public class UpdateCategoryActivity extends AppCompatActivity {

    EditText updateCategoryNameEDT, updateCategoryImageEDT;
    Button updateBTN;

    private FirebaseFirestore db;
    private DocumentReference categoryDocumentRef; // Reference to the specific document in Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_category);

        db = FirebaseFirestore.getInstance();

        // Retrieve the data you want to update from the Intent
        String catName = getIntent().getStringExtra("categoryName");
        String categoryUID = getIntent().getStringExtra("categoryUID");
        String categoryImage = getIntent().getStringExtra("categoryImageUrl");
        // Construct the document reference using the categoryUID
        System.out.println("6666 " + categoryUID);
        categoryDocumentRef = db.collection("categories").document(categoryUID);

        updateCategoryImageEDT = findViewById(R.id.updateEditTextCategoryImage);
        updateCategoryNameEDT = findViewById(R.id.updateEditTextCategoryName);
        updateBTN = findViewById(R.id.buttonUpdate);

        // Set the current values in the EditText fields
        updateCategoryNameEDT.setText(catName);
        updateCategoryImageEDT.setText(categoryImage);
        updateBTN.setOnClickListener(v -> {
            String updatedName = updateCategoryNameEDT.getText().toString();
            String updatedImage = updateCategoryImageEDT.getText().toString();

            Map<String, String> categoryModel = new HashMap<>();
            categoryModel.put("catImageUrl", updatedImage);
            categoryModel.put("catName", updatedName);
            categoryModel.put("uID", categoryUID);
            updateCategory(categoryModel);
        });
    }

    private void updateCategory(Map<String, String> categoryModel) {

        categoryDocumentRef
                .set(categoryModel)
                .addOnSuccessListener(aVoid -> {
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);

                    finish();
                })
                .addOnFailureListener(e -> {
                });
    }
}
