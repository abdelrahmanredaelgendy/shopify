package com.example.shopping.ui.products;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping.R;
import com.example.shopping.adapters.ProductAdapter;
import com.example.shopping.model.ProductModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SearchProductsActivity extends AppCompatActivity {

    private ImageView micIv, scanBarcodeIv;
    private EditText searchEt;
    private RecyclerView productsRv;

    SharedPreferences sharedPreferences;
    private FirebaseFirestore db;
    private List<ProductModel> productList;
    private ProductAdapter adapter;
    private String searchText;
    private boolean isAdmin;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);

        sharedPreferences = getSharedPreferences("adminPref", MODE_PRIVATE);
        isAdmin = sharedPreferences.getBoolean("admin", false);

        db = FirebaseFirestore.getInstance();

        micIv = findViewById(R.id.mic_iv);
        scanBarcodeIv = findViewById(R.id.scan_barcode_iv);
        searchEt = findViewById(R.id.search_et);
        productsRv = findViewById(R.id.products_rv);

        productsRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        productList = new ArrayList<>();

        adapter = new ProductAdapter(isAdmin, productList, new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ProductModel product) {
                Intent intent = new Intent(SearchProductsActivity.this, ProductDetailsActivity.class);
                intent.putExtra("PRODUCT_ID", product.getuID());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(ProductModel product, int position) {

            }

            @Override
            public void onEditClick(ProductModel product) {

            }
        });
        productsRv.setAdapter(adapter);

        searchEt.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm2.hideSoftInputFromWindow(searchEt.getWindowToken(), 0);

                if (!TextUtils.isEmpty(searchEt.getText().toString())) {
                    // Do search
                    searchText = searchEt.getText().toString().toLowerCase();
                    fetchProducts();
                } else {
                    productList.clear();
                    searchText = "";
                    adapter.notifyDataSetChanged();
                }

                return true;
            }
            return false;
        });

        micIv.setOnClickListener(view -> {
            Intent intent
                    = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
            } catch (Exception e) {
                Toast
                        .makeText(SearchProductsActivity.this, " " + e.getMessage(),
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });

        ScanOptions scanOptions = new ScanOptions();
        scanOptions.setBeepEnabled(true);
        scanOptions.setOrientationLocked(false);

        scanBarcodeIv.setOnClickListener(view -> barcodeLauncher.launch(new ScanOptions()));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchProducts() {
        db.collection("products")
                .whereGreaterThanOrEqualTo("productNameLowerCase", searchText)
                .whereLessThanOrEqualTo("productNameLowerCase", searchText + "\uf8ff") // \uf8ff is a placeholder to include all characters
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ProductModel product = document.toObject(ProductModel.class);
                            productList.add(product);
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                searchEt.setText(
                        Objects.requireNonNull(result).get(0));
                searchText = searchEt.getText().toString().toLowerCase();
                fetchProducts();
            }
        }
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    Intent intent = new Intent(SearchProductsActivity.this, ProductDetailsActivity.class);
                    intent.putExtra("PRODUCT_ID", result.getContents());
                    startActivity(intent);
                }
            });
}