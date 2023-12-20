package com.example.shopping.ui.products;

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
import com.example.shopping.adapters.ProductAdapter;
import com.example.shopping.model.ProductModel;
import com.example.shopping.ui.BestSellActivity;
import com.example.shopping.ui.cart.CartActivity;
import com.example.shopping.ui.categories.CategoryActivity;
import com.example.shopping.ui.orders.OrdersActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<ProductModel> productList;
    private ProductAdapter adapter;
    SharedPreferences sharedPreferences;
    private String categoryUid;
    boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        categoryUid = getIntent().getStringExtra("categoryUid");

        sharedPreferences = getSharedPreferences("adminPref", MODE_PRIVATE);
        isAdmin = sharedPreferences.getBoolean("admin", false);

        db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = findViewById(R.id.product_rv);
        ImageView orderHistoryIv, bestsellerChart;

        bestsellerChart = findViewById(R.id.show_chart_iv);
        orderHistoryIv = findViewById(R.id.past_order_iv);
        bestsellerChart.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        orderHistoryIv.setVisibility(isAdmin ? View.GONE : View.VISIBLE);

        orderHistoryIv.setOnClickListener(
                view ->
                {
                    if (!isAdmin) {
                        Intent intent = new Intent(ProductsActivity.this, OrdersActivity.class);
                        startActivity(intent);
                    }


                }
        );
        bestsellerChart.setOnClickListener(
                view ->
                {
                    if (isAdmin) {
                        Intent intent = new Intent(ProductsActivity.this, BestSellActivity.class);
                        startActivity(intent);
                    }


                }
        );
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        productList = new ArrayList<>();
        ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        clearProductsList();

                        fetchProducts();
                    }
                });

        ImageView cartIv = findViewById(R.id.cart_iv);
        cartIv.setOnClickListener(view ->

        {
            Intent intent;
            if (isAdmin) {
                intent = new Intent(ProductsActivity.this, OrdersActivity.class);
            } else {
                intent = new Intent(ProductsActivity.this, CartActivity.class);
            }

            activityResultLaunch.launch(intent);
        });
        ImageView searchIv = findViewById(R.id.search_iv);
        searchIv.setOnClickListener(view -> {
            Intent intent = new Intent(ProductsActivity.this, SearchProductsActivity.class);
            activityResultLaunch.launch(intent);
        });

        adapter = new ProductAdapter(isAdmin, productList, new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ProductModel product) {
                Intent intent = new Intent(ProductsActivity.this, ProductDetailsActivity.class);
                intent.putExtra("PRODUCT_ID", product.getuID());
                startActivity(intent);
            }


            public void onEditClick(ProductModel product) {
                Intent intent = new Intent(ProductsActivity.this, UpdateProductActivity.class);
                intent.putExtra("productName", product.getProductName());
                intent.putExtra("productUID", product.getuID());
                intent.putExtra("categoryUID", product.getCategoryUID());
                intent.putExtra("PRODUCT_DESC", product.getProductDesc());
                intent.putExtra("productImageUrl", product.getProductImageUrl());
                intent.putExtra("productPrice", product.getProductPrice());
                intent.putExtra("productCount", product.getProductCount());
                activityResultLaunch.launch(intent);
            }

            @Override
            public void onDeleteClick(ProductModel product, int position) {
                deleteCategory(product, position);
            }
        });
        recyclerView.setAdapter(adapter);

        fetchProducts();

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setVisibility(!isAdmin ? View.GONE : View.VISIBLE);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(ProductsActivity.this, AddProductActivity.class);
            intent.putExtra("categoryUid", categoryUid);
            activityResultLaunch.launch(intent);
        });
    }


    private void clearProductsList() {
        productList.clear();
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchProducts() {
        CollectionReference productsRef = db.collection("products");

        // Query for products where "categoryId" is equal to the specified categoryId
        Query query = productsRef.whereEqualTo("categoryUID", categoryUid);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    ProductModel product = document.toObject(ProductModel.class);
                    product.setuID(document.getId());
                    productList.add(product);
                }

                adapter.notifyDataSetChanged();
            } else {
                // Handle errors
                Exception exception = task.getException();
                if (exception != null) {
                    exception.printStackTrace();
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteCategory(ProductModel product, int position) {
        if (product != null) {
            if (position != -1) {
                db.collection("products").document(product.getuID())
                        .delete()
                        .addOnSuccessListener(aVoid -> runOnUiThread(() -> {
                            productList.remove(position);
                            adapter.notifyItemRemoved(position);
                        }))
                        .addOnFailureListener(Throwable::printStackTrace);
            }
        }
    }

}
