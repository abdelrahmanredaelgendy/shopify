package com.example.shopping.ui.products;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.shopping.R;
import com.example.shopping.model.CartModel;
import com.example.shopping.model.CartProductModel;
import com.example.shopping.model.ProductModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    CartModel cartModel;
    private TextView productNameTv, productPriceTv, productDescTv;
    private ImageView productIv, productBarcodeIv;
    private Button addToCartBtn;

    private String productUid, productName, productDesc, productImageUrl;
    private int productCount, productCartCount;
    private double productPrice;
    String userToken;
    ProductModel product;
    SharedPreferences sharedPreferences;
    boolean isAddToCartBtnActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("adminPref", MODE_PRIVATE);
        userToken = sharedPreferences.getString("userToken", "");
        boolean isAdmin = sharedPreferences.getBoolean("admin" ,false);
        productIv = findViewById(R.id.product_iv);
        productBarcodeIv = findViewById(R.id.product_barcode_iv);
        productNameTv = findViewById(R.id.product_title_tv);
        productDescTv = findViewById(R.id.product_desc_tv);
        productPriceTv = findViewById(R.id.product_price_tv);
        addToCartBtn = findViewById(R.id.product_add_to_cart_btn);
        addToCartBtn.setVisibility(isAdmin ? View.GONE: View.VISIBLE);

        productUid = getIntent().getStringExtra("PRODUCT_ID");

        addToCartBtn.setOnClickListener(view -> {
            if (isAddToCartBtnActive) {
                fetchActiveCart();
            }
        });

        fetchDetailsProduct();
    }

    private void fetchDetailsProduct() {
        CollectionReference productDetailsRef = db.collection("products");
        Query query = productDetailsRef.whereEqualTo("uID", productUid);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot document = task.getResult();
                product = document.toObjects(ProductModel.class).get(0);

                setData();
                setViews();
            } else {
                Exception exception = task.getException();
                if (exception != null) {
                    exception.printStackTrace();
                }
            }
        });

    }

    private void setViews() {
        productNameTv.setText(productName);
        productDescTv.setText(productDesc);
        productPriceTv.setText(String.valueOf(productPrice));
        Glide.with(this).load(productImageUrl).into(productIv);
        genBarcode();
    }

    private void setData() {
        productName = product.getProductName();
        productDesc = product.getProductDesc();
        productImageUrl = product.getProductImageUrl();
        productPrice = product.getProductPrice();
        productCount = product.getProductCount();
        productCartCount = product.getProductCartCount();
    }

    private void fetchActiveCart() {
        isAddToCartBtnActive = false;

        CollectionReference cartsRef = db.collection("carts");

        Query query = cartsRef.whereEqualTo("userUid", userToken).whereEqualTo("isActive", 1);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    cartModel = document.toObject(CartModel.class);
                }

                addProductToCart();
            } else {
                Exception exception = task.getException();
                if (exception != null) {
                    exception.printStackTrace();
                }
            }
        });
    }

    private void addProductToCart() {

        if (productCount == 0) {
            Toast.makeText(this, "Sorry, this item is out of stock", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("carts");
        List<CartProductModel> newProductsList = new ArrayList<>();
        String generatedId;
        int productsCount;
        double totalPrice;

        if (cartModel != null) {
            newProductsList.addAll(cartModel.getProductCartModelList());
            boolean productFound = false;

            for (CartProductModel model : newProductsList) {
                if (model.getProductUid().equals(productUid)) {
                    productFound = true;

                    if (productCount <= model.getProductCartCount()) {
                        Toast.makeText(this, "Sorry, this item is out of stock", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        model.setProductCartCount(model.getProductCartCount() + 1);
                    }
                    break;
                }
            }

            if (!productFound) {
                CartProductModel cartProductModel = new CartProductModel();
                cartProductModel.setProductUid(productUid);
                cartProductModel.setProductCartCount(1);
                newProductsList.add(cartProductModel);
            }

            generatedId = cartModel.getUid();
            productsCount = cartModel.getProductsCount() + 1;
            totalPrice = cartModel.getTotalPrice() + productPrice;
        } else {
            CartProductModel cartProductModel = new CartProductModel();
            cartProductModel.setProductUid(productUid);
            cartProductModel.setProductCartCount(1);
            newProductsList.add(cartProductModel);

            DocumentReference documentReference = collectionReference.document();
            generatedId = documentReference.getId();
            productsCount = 1;
            totalPrice = productPrice;
        }

        Map<String, Object> cartData = new HashMap<>();
        cartData.put("uid", generatedId);
        cartData.put("userUid", userToken);
        cartData.put("productCartModelList", newProductsList);
        cartData.put("productsCount", productsCount);
        cartData.put("totalPrice", totalPrice);
        cartData.put("isActive", 1);

        collectionReference.document(generatedId)
                .set(cartData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show();
                    }

                    isAddToCartBtnActive = true;
                });
    }

    private void genBarcode() {

        MultiFormatWriter mwriter = new MultiFormatWriter();

        try {
            // Generating a barcode matrix
            BitMatrix matrix = mwriter.encode(productUid, BarcodeFormat.CODE_128, productBarcodeIv.getWidth(), productBarcodeIv.getHeight());

            Bitmap bitmap = Bitmap.createBitmap(productBarcodeIv.getWidth(), productBarcodeIv.getHeight(), Bitmap.Config.RGB_565);

            for (int i = 0; i < productBarcodeIv.getWidth(); i++) {
                for (int j = 0; j < productBarcodeIv.getHeight(); j++) {
                    bitmap.setPixel(i, j, matrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }

            productBarcodeIv.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(this, "Exception " + e, Toast.LENGTH_SHORT).show();
        }
    }

}