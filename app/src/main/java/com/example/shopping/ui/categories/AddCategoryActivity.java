package com.example.shopping.ui.categories;

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

public class AddCategoryActivity extends AppCompatActivity {
    private EditText editTextCategoryName;
    private EditText editTextCategoryImage;
    private Button buttonSave;

    private FirebaseFirestore fireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        fireStore = FirebaseFirestore.getInstance();
        editTextCategoryName = findViewById(R.id.editTextCategoryName);
        editTextCategoryImage = findViewById(R.id.editTextCategoryImage);
        buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(view -> saveDataToFireStore());
    }

    private void saveDataToFireStore() {
        String categoryName = editTextCategoryName.getText().toString().trim();
        String categoryImage = editTextCategoryImage.getText().toString().trim();

        if (categoryName.isEmpty() || categoryImage.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }


        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("categories");
        DocumentReference documentReference = collectionReference.document();
        String generatedId = documentReference.getId();
        Map<String, Object> categoryData = new HashMap<>();
        categoryData.put("catName", categoryName);
        categoryData.put("catImageUrl", categoryImage);
        categoryData.put("uID", generatedId);
        // Add data to Firestore
        documentReference
                .set(categoryData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(AddCategoryActivity.this, CategoryActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                    }
                });
    }
}