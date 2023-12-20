package com.example.shopping.ui.auth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shopping.R;
import com.example.shopping.ui.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText emailEDT, passwordEDT, retypePasswordEDT;
    Button registerBTN;
    TextView navSignInTV;
    ProgressBar progressbar;
    FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    Button birthdayButton;
    Calendar selectedDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        emailEDT = findViewById(R.id.registerEmailEDT);
        passwordEDT = findViewById(R.id.registerPasswdEDT);
        registerBTN = findViewById(R.id.registerBTN);
        progressbar = findViewById(R.id.progressbar);
        navSignInTV = findViewById(R.id.navSignInTV);
        retypePasswordEDT = findViewById(R.id.registerRetypePasswordEDT);
        birthdayButton = findViewById(R.id.birthdayButton);
        birthdayButton.setOnClickListener(v -> showDatePickerDialog());
        navSignInTV.setOnClickListener(v -> signIn());
        registerBTN.setOnClickListener(v -> registerUser());
    }

    public void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    selectedDate.set(year, monthOfYear, dayOfMonth);
                    updateButtonText();

                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        // Set the maximum allowed date to today
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void updateButtonText() {
        // Format the selected date to a readable string
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(selectedDate.getTime());

        // Set the button text with the formatted date
        birthdayButton.setText(formattedDate);
    }

    private void signIn() {
        Intent intent
                = new Intent(RegisterActivity.this,
                LoginActivity.class);
        startActivity(intent);
    }

    private void registerUser() {
        String email, password, retypePassword;
        email = emailEDT.getText().toString();
        password = passwordEDT.getText().toString();
        retypePassword = retypePasswordEDT.getText().toString();
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
        if (TextUtils.isEmpty(retypePassword)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter retype password first!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (!password.equals(retypePassword)) {
            Toast.makeText(getApplicationContext(),
                            "Password must be matched",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        progressbar.setVisibility(View.VISIBLE);
        mAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(),
                                        "Registration successful!",
                                        Toast.LENGTH_LONG)
                                .show();
                        saveDateToFirestore();
                        progressbar.setVisibility(View.GONE);

                    } else {
                        Toast.makeText(
                                        getApplicationContext(),
                                        "Registration failed!!"
                                                + " Please try again later",
                                        Toast.LENGTH_LONG)
                                .show();
                        progressbar.setVisibility(View.GONE);
                    }
                });


    }

    private void saveDateToFirestore() {
            String userId = mAuth.getCurrentUser().getUid();
            DocumentReference userDocument = firestore.collection("users").document(userId);
            Map<String, Object> userData = new HashMap<>();
            userData.put("birthday", birthdayButton.getText());
            userDocument.set(userData).addOnCompleteListener(
                    task -> {
                        if (task.isSuccessful()) {
                            Intent intent
                                    = new Intent(RegisterActivity.this,
                                    LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(
                                            getApplicationContext(),
                                            "Registration failed!!"
                                                    + " Please try again later",
                                            Toast.LENGTH_LONG)
                                    .show();

                        }
                    }
            );
    }
}