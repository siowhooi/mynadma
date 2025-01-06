package com.example.mynadma;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewOfflineDisasterGuideActivity extends AppCompatActivity{
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    private DatabaseReference databaseReference;
    private TextView titleTextView, beforeTextView, duringTextView, afterTextView;
    private TextView beforeTitle, duringTitle, afterTitle, subtitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_offline_guide);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the navigate up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Set navigation click listener
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // Initialize UI elements
        titleTextView = findViewById(R.id.titleText);
        beforeTextView = findViewById(R.id.beforeContent);
        duringTextView = findViewById(R.id.duringContent);
        afterTextView = findViewById(R.id.afterContent);

        beforeTitle = findViewById(R.id.beforeTitle);
        duringTitle = findViewById(R.id.duringTitle);
        afterTitle = findViewById(R.id.afterTitle);
        subtitle = findViewById(R.id.subtitleText);

        // Initialize Firebase Database reference
        String guide = getIntent().getStringExtra("disasterName");
        String reference = "disasterGuide" + "/" + guide;
        databaseReference = FirebaseDatabase.getInstance().getReference(reference);

        // Fetch and display disaster guide data
        fetchDisasterGuideData();

    }

    private void fetchDisasterGuideData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Fetch data from the database and update the UI
                    OfflineGuide guide = snapshot.getValue(OfflineGuide.class);

                    titleTextView.setText(guide.getTitle());
                    beforeTextView.setText(guide.getBefore());
                    duringTextView.setText(guide.getDuring());
                    afterTextView.setText(guide.getAfter());

                    String disasterName = getIntent().getStringExtra("disasterName");
                    beforeTitle.setText("Before the " + disasterName);
                    duringTitle.setText("During the " + disasterName);
                    afterTitle.setText("Before the " + disasterName);
                    subtitle.setText("Stay safe during " + disasterName);

                } else {
                    Toast.makeText(ViewOfflineDisasterGuideActivity.this, "No data found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(ViewOfflineDisasterGuideActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}