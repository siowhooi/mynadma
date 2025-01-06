package com.example.mynadma;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.Manifest;

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

        String guide = getIntent().getStringExtra("disasterName");
        String filename = guide + ".json";
        //  request to read storage
        if (ContextCompat.checkSelfPermission(ViewOfflineDisasterGuideActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ViewOfflineDisasterGuideActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        // Get the path to the public external storage directory
        File file = new File(getFilesDir(), filename);
        if (file.exists()) {
            // if file exists then load from file
            readFromFile(file);
            Log.d("stupid", file.getAbsolutePath());
        } else {
            // else load from database
            // Initialize Firebase Database reference
            Toast.makeText(ViewOfflineDisasterGuideActivity.this, "Cannot load from file, fetching from database.", Toast.LENGTH_SHORT).show();
            String reference = "disasterGuide" + "/" + guide;
            databaseReference = FirebaseDatabase.getInstance().getReference(reference);
            // Fetch and display disaster guide data
            fetchDisasterGuideData();
        }
    }

    private void readFromFile(File file) {
        StringBuilder json = new StringBuilder();
        try {
            /// Open a file input stream to read from external storage
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(fis);

            // Read the file content into a string
            int character;
            while ((character = reader.read()) != -1) {
                json.append((char) character);
            }

            // Close the reader
            reader.close();

            // Convert the JSON string back into a Java object using Gson
            Gson gson = new Gson();
            OfflineGuide guide = gson.fromJson(json.toString(), OfflineGuide.class);
            Toast.makeText(ViewOfflineDisasterGuideActivity.this, "Guide loaded from local file.", Toast.LENGTH_SHORT).show();
            updateUI(guide);

        } catch (IOException e) {
            Toast.makeText(ViewOfflineDisasterGuideActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            Toast.makeText(ViewOfflineDisasterGuideActivity.this, "Fetching guide from database.", Toast.LENGTH_SHORT).show();
            fetchDisasterGuideData();
        }
    }

    private void fetchDisasterGuideData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Fetch data from the database and update the UI
                    OfflineGuide guide = snapshot.getValue(OfflineGuide.class);
                    if(guide != null) {
                        updateUI(guide);
                    } else {
                        Toast.makeText(ViewOfflineDisasterGuideActivity.this, "Cannot load from database.", Toast.LENGTH_SHORT).show();
                    }

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

    private void updateUI(OfflineGuide guide) {
        // updates the UI
        titleTextView.setText(guide.getTitle());
        beforeTextView.setText(guide.getBefore());
        duringTextView.setText(guide.getDuring());
        afterTextView.setText(guide.getAfter());

        String disasterName = getIntent().getStringExtra("disasterName");
        beforeTitle.setText("Before the " + disasterName);
        duringTitle.setText("During the " + disasterName);
        afterTitle.setText("Before the " + disasterName);
        subtitle.setText("Stay safe during " + disasterName);
    }
}