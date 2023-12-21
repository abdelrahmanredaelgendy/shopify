package com.example.shopping.ui.orders;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping.R;
import com.example.shopping.adapters.OrderProductsAdapter;
import com.example.shopping.model.CartModel;
import com.example.shopping.model.CartProductModel;
import com.example.shopping.model.ProductModel;
import com.example.shopping.ui.RatingAndFeedActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<ProductModel> productList;
    private OrderProductsAdapter adapter;
    CartModel orderModel;
    SharedPreferences sharedPreferences;

    ImageView rateIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        RecyclerView recyclerView = findViewById(R.id.order_products_rv);
        sharedPreferences = getSharedPreferences("adminPref", MODE_PRIVATE);
        rateIV=findViewById(R.id.rate_iv);
        db = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        productList = new ArrayList<>();
       boolean isAdmin = sharedPreferences.getBoolean("admin", false);
        Intent intent = getIntent();
        orderModel = intent.getParcelableExtra("orderModel");
        rateIV.setVisibility(isAdmin ? View.GONE: View.VISIBLE);
        rateIV.setOnClickListener(
                v -> {

                    RatingAndFeedActivity ratingDialog = new RatingAndFeedActivity(this,orderModel);
                    ratingDialog.show();

                }
        );
        adapter = new OrderProductsAdapter(productList, product -> {
        });
        recyclerView.setAdapter(adapter);

        fetchCartProducts();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchCartProducts() {
        List<String> productIds = new ArrayList<>();
        for (CartProductModel currentCartModel : orderModel.getProductCartModelList()) {
            productIds.add(currentCartModel.getProductUid());
        }
        Query query = db.collection("products").whereIn("uID", productIds);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                productList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    ProductModel product = document.toObject(ProductModel.class);
                    CartProductModel cartProductModel = findCartProductById(product.getuID(), orderModel.getProductCartModelList());
                    product.setProductCartCount(cartProductModel.getProductCartCount());
                    productList.add(product);
                }

                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public CartProductModel findCartProductById(String productId, List<CartProductModel> cartProductModelList) {
        for (CartProductModel product : cartProductModelList) {
            if (product.getProductUid().equals(productId)) {
                return product;
            }
        }

        return null;
    }
}