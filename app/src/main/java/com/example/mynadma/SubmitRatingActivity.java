package com.example.mynadma;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SubmitRatingActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private Button submitButton, deleteButton;
    private DatabaseReference ratingRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String city = getIntent().getStringExtra("city");
        String state = getIntent().getStringExtra("state");
        String userId = getIntent().getStringExtra("userId");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_rating);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the navigate up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Set navigation click listener
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // setting the form heading
        TextView formTownState = (TextView) findViewById(R.id.formTownState);
        String cityState = city + ", " + state;
        formTownState.setText(cityState);

        // setting the database reference
        String collectionRef = "networkRating/" + state.toLowerCase() + "/" + city.toLowerCase();
        ratingRef = FirebaseDatabase.getInstance().getReference(collectionRef);

        // setting the rating bar
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming the value is a float
                    Float rating = dataSnapshot.child("userRating").child(userId).getValue(Float.class);
                    if (rating != null) {
                        ratingBar.setRating(rating); // Set the rating in the RatingBar
                    } else {
                        ratingBar.setRating(0);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(SubmitRatingActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // setting the submit button
        submitButton = (Button) findViewById(R.id.submitRatingButton);
        submitButton.setOnClickListener(view -> {
            float rating = ratingBar.getRating(); // Get the current rating from the RatingBar
            // submit the new rating
            ratingRef.child("userRating").child(userId).setValue(rating)
                    .addOnSuccessListener(aVoid -> {
                        // Success: Rating submitted
                        Toast.makeText(this, "Rating submitted successfully", Toast.LENGTH_SHORT).show();
                        finish(); // End the activity and go back to the previous one
                    })
                    .addOnFailureListener(e -> {
                        // Failure: Handle the error
                        Toast.makeText(this, "Failed to submit rating: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // setting the delete button
        deleteButton = (Button) findViewById(R.id.deleteRatingButton);
        deleteButton.setOnClickListener(view -> {
            ratingRef.child("userRating").child(userId).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        // Success: Rating submitted
                        Toast.makeText(this, "Rating removed successfully", Toast.LENGTH_SHORT).show();
                        ratingBar.setRating(0);
                    })
                    .addOnFailureListener(e -> {
                        // Failure: Handle the error
                        Toast.makeText(this, "Failed to delete rating: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // update the average when data change
        String userRatingPath = "networkRating/" + state.toLowerCase() + "/" + city.toLowerCase() + "/userRating";
        DatabaseReference userRatingRef = FirebaseDatabase.getInstance().getReference(userRatingPath);
        // updates the average rating when user update their rating
        userRatingRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float total = 0;
                int length = 0;
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Integer rating = childSnapshot.getValue(Integer.class); // Get the rating value (as Integer)

                    total += rating;
                    length += 1;
                }

                if (length > 0) {
                    ratingRef.child("rating").setValue(total / length);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", "onCancelled: Failed to calculate the new average");
            }
        });
    }
}