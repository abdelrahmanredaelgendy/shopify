package com.example.shopping.ui.orders;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.shopping.R;
import com.example.shopping.adapters.OrdersAdapter;
import com.example.shopping.model.CartModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private List<CartModel> ordersList;
    private OrdersAdapter adapter;
    SharedPreferences sharedPreferences;
    private SwipeRefreshLayout swipeRefreshLayout;
    boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        db = FirebaseFirestore.getInstance();

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        RecyclerView recyclerView = findViewById(R.id.orders_rv);
        sharedPreferences = getSharedPreferences("adminPref", MODE_PRIVATE);
        isAdmin = sharedPreferences.getBoolean("admin", false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ordersList = new ArrayList<>();

        adapter = new OrdersAdapter(ordersList, orderItem -> {
            Intent intent = new Intent(OrdersActivity.this, OrderDetailsActivity.class);
            intent.putExtra("orderModel", orderItem);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this::fetchOrders);

        fetchOrders();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchOrders() {
        String userId = sharedPreferences.getString("userToken", "");
        if (isAdmin) {
            db.collection("carts")
                    .whereEqualTo("isActive", 0)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        ordersList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            CartModel category = document.toObject(CartModel.class);
                            ordersList.add(category);
                        }
                        adapter.notifyDataSetChanged();

                        swipeRefreshLayout.setRefreshing(false);
                    })
                    .addOnFailureListener(e -> swipeRefreshLayout.setRefreshing(false));
        } else {
            CollectionReference productsRef = db.collection("carts");
            // Query for products where "categoryId" is equal to the specified categoryId
            Query query = productsRef.whereEqualTo("isActive", 0).whereEqualTo("userUid", userId);
            query.get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        ordersList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            CartModel category = document.toObject(CartModel.class);
                            ordersList.add(category);
                        }
                        adapter.notifyDataSetChanged();

                        swipeRefreshLayout.setRefreshing(false);
                    })
                    .addOnFailureListener(e -> swipeRefreshLayout.setRefreshing(false));

        }

    }
}