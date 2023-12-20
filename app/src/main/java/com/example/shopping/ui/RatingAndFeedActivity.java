package com.example.shopping.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.shopping.R;
import com.example.shopping.model.CartModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RatingAndFeedActivity extends Dialog implements View.OnClickListener {

    private RatingBar ratingBar;
    private EditText feedbackEditText;
    private Button submitButton;
    private FirebaseFirestore db;
    CartModel cartModel;

    public RatingAndFeedActivity(@NonNull Context context, CartModel cartModel) {
        super(context);
        db = FirebaseFirestore.getInstance();
        this.cartModel = cartModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rating_and_feed); // Replace with your actual XML layout file
        ratingBar = findViewById(R.id.ratingBar);
        feedbackEditText = findViewById(R.id.feedbackEditText);
        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.submitButton) {
            float rating = ratingBar.getRating();
            String feedback = feedbackEditText.getText().toString();
            addRatingToFirestore(rating, feedback);
            dismiss();
        }
    }

    private void addRatingToFirestore(float rating, String feedback) {
        Map<String, Object> ratingData = new HashMap<>();
        ratingData.put("rating", rating);
        ratingData.put("feedback", feedback);
        db.collection("ratings").document(cartModel.getUid())
                .set(ratingData)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(getContext(), "Rating added successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error adding rate", Toast.LENGTH_SHORT).show());
    }

}

