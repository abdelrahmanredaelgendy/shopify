package com.example.shopping.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopping.R;
import com.example.shopping.ui.categories.CategoryActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText emailEDT, passwordEDT;
    Button loginBTN;
    ProgressBar progressbar;
    TextView navSignUpTV;
    TextView forgotPasswordTV;

    FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        emailEDT = findViewById(R.id.loginEmailEDT);
        passwordEDT = findViewById(R.id.loginPasswdEDT);
        loginBTN = findViewById(R.id.loginBTN);
        progressbar = findViewById(R.id.progressbar);
        navSignUpTV = findViewById(R.id.navSignUpTV);
        sharedPreferences = getSharedPreferences("adminPref", MODE_PRIVATE);
        forgotPasswordTV = findViewById(R.id.forgotPasswordTV);
        forgotPasswordTV.setOnClickListener(v -> {
            handleForgotPassword();
        });
        navSignUpTV.setOnClickListener(v -> signUp());
        loginBTN.setOnClickListener(v -> loginAccount());
    }

    private void signUp() {
        Intent intent
                = new Intent(LoginActivity.this,
                RegisterActivity.class);
        startActivity(intent);
    }

    void loginAccount() {
        String email, password;
        email = emailEDT.getText().toString();
        password = passwordEDT.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter email first!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter password first!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        progressbar.setVisibility(View.VISIBLE);
        loginBTN.setEnabled(false);
        passwordEDT.setEnabled(false);
        emailEDT.setEnabled(false);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                Toast.makeText(getApplicationContext(),
                                                "Login successful!!",
                                                Toast.LENGTH_LONG)
                                        .show();
                                progressbar.setVisibility(View.GONE);
                                if (email.equals("boodyg620@gmail.com")) {
                                    editor.putBoolean("admin", true).apply();

                                } else {
                                    editor.putBoolean("admin", false).apply();
                                }

                                System.out.println("userToken::: " + mAuth.getCurrentUser().getUid());
                                editor.putString("userToken", Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).apply();
                                editor.putString("userEmail", Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()).apply();

                                Intent intent
                                        = new Intent(LoginActivity.this,
                                        CategoryActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(),
                                                "Login failed!!",
                                                Toast.LENGTH_LONG)
                                        .show();
                                progressbar.setVisibility(View.GONE);
                                loginBTN.setEnabled(true);
                                passwordEDT.setEnabled(true);
                                emailEDT.setEnabled(true);
                            }
                        });
    }
     void handleForgotPassword()
     {
        String email = emailEDT.getText().toString().trim();

        if (!TextUtils.isEmpty(email)) {
            progressbar.setVisibility(View.VISIBLE);

            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                progressbar.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(LoginActivity.this, "Enter your registered email first.", Toast.LENGTH_SHORT).show();
        }
    }
}
