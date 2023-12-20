package com.example.shopping.ui.categories;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping.R;
import com.example.shopping.adapters.CategoryAdapter;
import com.example.shopping.model.CategoryModel;
import com.example.shopping.ui.cart.CartActivity;
import com.example.shopping.ui.orders.OrdersActivity;
import com.example.shopping.ui.products.ProductsActivity;
import com.example.shopping.ui.products.SearchProductsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<CategoryModel> categoryList;
    private CategoryAdapter adapter;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = findViewById(R.id.category_rv);
        sharedPreferences = getSharedPreferences("adminPref", MODE_PRIVATE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        categoryList = new ArrayList<>();

        ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        clearCategoriesList();

                        fetchCategories();
                    }
                });

        adapter = new CategoryAdapter(this, categoryList, new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CategoryModel category) {
                Intent intent = new Intent(CategoryActivity.this, ProductsActivity.class);
                intent.putExtra("categoryUid", category.getuID());
                startActivity(intent);
            }

            public void onEditClick(CategoryModel category) {
                // Handle edit click
                // You can start a new activity for updating the category details
                Intent intent = new Intent(CategoryActivity.this, UpdateCategoryActivity.class);

                intent.putExtra("categoryName", category.getCatName());
                intent.putExtra("categoryUID", category.getuID());
                intent.putExtra("categoryImageUrl", category.getCatImageUrl());

                activityResultLaunch.launch(intent);
            }

            @Override
            public void onDeleteClick(CategoryModel category, int position) {
                deleteCategory(category, position);
            }
        });
        recyclerView.setAdapter(adapter);

        fetchCategories();

        FloatingActionButton fab = findViewById(R.id.fab);
        boolean admin = sharedPreferences.getBoolean("admin", false);
        fab.setVisibility(!admin ? View.GONE : View.VISIBLE);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(CategoryActivity.this, AddCategoryActivity.class);
            activityResultLaunch.launch(intent);
        });

        ImageView cartIv = findViewById(R.id.cart_iv);
        cartIv.setOnClickListener(view -> {
            Intent intent;
            if (admin) {
                intent = new Intent(CategoryActivity.this, OrdersActivity.class);
            } else {
                intent = new Intent(CategoryActivity.this, CartActivity.class);
            }

            activityResultLaunch.launch(intent);
        });

        ImageView searchIv = findViewById(R.id.search_iv);
        searchIv.setOnClickListener(view -> {
            Intent intent = new Intent(CategoryActivity.this, SearchProductsActivity.class);
            activityResultLaunch.launch(intent);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clearCategoriesList() {
        categoryList.clear();
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchCategories() {
        db.collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        CategoryModel category = document.toObject(CategoryModel.class);
                        category.setuID(document.getId());
                        categoryList.add(category);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteCategory(CategoryModel category, int position) {
        if (category != null) {
            if (position != -1) {
                db.collection("categories").document(category.getuID())
                        .delete()
                        .addOnSuccessListener(aVoid -> runOnUiThread(() -> {
                            categoryList.remove(position);
                            adapter.notifyItemRemoved(position);
                        }))
                        .addOnFailureListener(Throwable::printStackTrace);
            }
        }
    }

}
