package com.example.shopping.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopping.R;
import com.example.shopping.ui.auth.LoginActivity;
import com.example.shopping.ui.categories.CategoryActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore fireStore;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("adminPref", MODE_PRIVATE);
        new Handler().postDelayed(() -> {
            String userToken = sharedPreferences.getString("userToken", "");
            Intent i;

            if (userToken.isEmpty()) {
                i = new Intent(MainActivity.this, LoginActivity.class);
            } else {
                i = new Intent(MainActivity.this, CategoryActivity.class);
            }

            startActivity(i);
            finish();
        }, 2000);
    }
}