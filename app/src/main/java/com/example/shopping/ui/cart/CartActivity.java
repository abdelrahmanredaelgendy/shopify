package com.example.shopping.ui.cart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping.R;
import com.example.shopping.adapters.CartProductsAdapter;
import com.example.shopping.model.CartModel;
import com.example.shopping.model.CartProductModel;
import com.example.shopping.model.ProductModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<ProductModel> productList;
    private CartProductsAdapter adapter;
    ConstraintLayout constraintLayout ;
    TextView productsCountTv, totalPriceTv;


    SharedPreferences sharedPreferences;
    String userToken;
    CartModel currentCartModel;
    Button confirmOrderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        confirmOrderBtn = findViewById(R.id.confirm_order_btn);
        RecyclerView recyclerView = findViewById(R.id.products_rv);
        productsCountTv = findViewById(R.id.products_count_tv);
        totalPriceTv = findViewById(R.id.total_price_tv);

        sharedPreferences = getSharedPreferences("adminPref", MODE_PRIVATE);
        userToken = sharedPreferences.getString("userToken", "");
        constraintLayout =findViewById(R.id.info_add_cart_btn);

        db = FirebaseFirestore.getInstance();

        fetchActiveCart();


        sharedPreferences = getSharedPreferences("adminPref", MODE_PRIVATE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        productList = new ArrayList<>();

        adapter = new CartProductsAdapter(this, productList, new CartProductsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ProductModel product) {

            }

            @Override
            public void onDeleteClick(ProductModel product, int position) {
                currentCartModel.getProductCartModelList().removeIf(currentCartModel -> currentCartModel.getProductUid().equals(product.getuID()));
                updateCart(position);
            }

            @Override
            public void onIncreaseProductClick(ProductModel product, int position) {
                changeProductCount(product, position, true);
            }

            @Override
            public void onDecreaseProductClick(ProductModel product, int position) {
                changeProductCount(product, position, false);
            }
        });
        recyclerView.setAdapter(adapter);

        confirmOrderBtn.setOnClickListener(view -> {
            if (currentCartModel != null) {
                confirmCart();
            }
        });
    }

    private void changeProductCount(ProductModel product, int position, boolean isIncreasing) {

        List<CartProductModel> tempList = new ArrayList<>();
        Iterator<CartProductModel> iterator = currentCartModel.getProductCartModelList().iterator();

        while (iterator.hasNext()) {
            CartProductModel cartProductModel = iterator.next();

            if (cartProductModel.getProductUid().equals(product.getuID())) {
                // Modify the existing product
                if (isIncreasing) {
                    if (product.getProductCartCount() < product.getProductCount()) {
                        cartProductModel.setProductCartCount(cartProductModel.getProductCartCount() + 1);
                        currentCartModel.setProductsCount(currentCartModel.getProductsCount() + 1);
                        currentCartModel.setTotalPrice(currentCartModel.getTotalPrice() + product.getProductPrice());
                    } else {
                        Toast.makeText(this, "Sorry, this item is out of stock", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (cartProductModel.getProductCartCount() != 1) {
                        cartProductModel.setProductCartCount(cartProductModel.getProductCartCount() - 1);
                        currentCartModel.setProductsCount(currentCartModel.getProductsCount() - 1);
                        currentCartModel.setTotalPrice(currentCartModel.getTotalPrice() - product.getProductPrice());
                    }
                }

                // Add the modified product to tempList
                tempList.add(cartProductModel);

                // Remove the product from the original list
                iterator.remove();
            } else {
                // Add other products directly to tempList
                tempList.add(cartProductModel);
            }
        }

        // Add the tempList back to the original list
        currentCartModel.setProductCartModelList(tempList);

        updateCart(position);
    }

    private void clearProductsList() {
        productList.clear();
        adapter.notifyDataSetChanged();
    }

    private void fetchActiveCart() {
        CollectionReference cartsRef = db.collection("carts");

        // Query for products where "categoryId" is equal to the specified categoryId
        Query query = cartsRef.whereEqualTo("userUid", userToken).whereEqualTo("isActive", 1);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    currentCartModel = document.toObject(CartModel.class);
                }

                fetchCartProducts();

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
    private void fetchCartProducts() {
        if (currentCartModel != null) {

            constraintLayout.setVisibility(View.VISIBLE);

            List<String> productIds = new ArrayList<>();
            for (CartProductModel currentCartModel : currentCartModel.getProductCartModelList()) {
                productIds.add(currentCartModel.getProductUid());
            }

            // Create a query to get products where the document ID is in the list of productIds
            Query query = db.collection("products").whereIn("uID", productIds);

            // Execute the query
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    // Iterate through the result set and convert each document to a ProductModel
                    productList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ProductModel product = document.toObject(ProductModel.class);
                        //set cart product count.
                        CartProductModel cartProductModel = findCartProductById(product.getuID(), currentCartModel.getProductCartModelList());
                        product.setProductCartCount(cartProductModel.getProductCartCount());
                        totalPriceTv.setText(String.valueOf(currentCartModel.getTotalPrice()));
                        productsCountTv.setText(String.valueOf(currentCartModel.getProductsCount()));
                        productList.add(product);
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            constraintLayout.setVisibility(View.GONE);
        }
    }

    public CartProductModel findCartProductById(String productId, List<CartProductModel> cartProductModelList) {
        for (CartProductModel product : cartProductModelList) {
            if (product.getProductUid().equals(productId)) {
                // Found the product with the given ID
                return product;
            }
        }

        // Product not found
        return null;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateCart(int position) {
        Map<String, Object> cartModel = new HashMap<>();
        cartModel.put("uid", currentCartModel.getUid());
        cartModel.put("userUid", userToken);
        cartModel.put("productCartModelList", currentCartModel.getProductCartModelList());
        cartModel.put("productsCount", currentCartModel.getProductsCount());
        cartModel.put("totalPrice", currentCartModel.getTotalPrice());
        cartModel.put("isActive", 1);

        db.collection("carts").document(currentCartModel.getUid())
                .set(cartModel)
                .addOnSuccessListener(aVoid -> runOnUiThread(() -> {
                    fetchCartProducts();
                    adapter.notifyDataSetChanged();
                }))
                .addOnFailureListener(e -> {
                });
    }

    private void confirmCart() {
        Map<String, Object> cartModel = new HashMap<>();
        cartModel.put("uid", currentCartModel.getUid());
        cartModel.put("userUid", userToken);
        cartModel.put("productCartModelList", currentCartModel.getProductCartModelList());
        cartModel.put("productsCount", currentCartModel.getProductsCount());
        cartModel.put("totalPrice", currentCartModel.getTotalPrice());
        cartModel.put("isActive", 0);
        List<CartProductModel> allProducts = currentCartModel.getProductCartModelList();
        for (CartProductModel cartProductModel : allProducts) {
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).getuID().equals(cartProductModel.getProductUid())) {
                    int count = productList.get(i).getProductCount();

                    db.collection("products").document(cartProductModel.getProductUid()).update("productCount", count - cartProductModel.getProductCartCount());
                    db.collection("best_product")
                            .document(cartProductModel.getProductUid())
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    int currentSoldCount = documentSnapshot.getLong("soldCount").intValue();
                                    db.collection("best_product")
                                            .document(cartProductModel.getProductUid())
                                            .update("soldCount", currentSoldCount + cartProductModel.getProductCartCount());
                                } else {
                                    // If the document doesn't exist
                                    Map<String, Object> bestProductData = new HashMap<>();
                                    bestProductData.put("soldCount", cartProductModel.getProductCartCount());
                                    db.collection("best_product")
                                            .document(cartProductModel.getProductUid())
                                            .set(bestProductData);
                                }
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure
                            });
                }
            }

        }

        db.collection("carts").document(currentCartModel.getUid())
                .set(cartModel)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Order confirmed", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);

                    finish();
                })
                .addOnFailureListener(e -> {
                });
    }
}
