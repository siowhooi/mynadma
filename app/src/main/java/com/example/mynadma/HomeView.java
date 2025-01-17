package com.example.mynadma;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeView extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DatabaseReference databaseReference;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_view);

        // setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // setup drawer layout
        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation_view);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                0, 0);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // setup buttons
        Button offlineGuide = (Button) findViewById(R.id.offline_guide);
        offlineGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent offlineCache = new Intent(HomeView.this, OfflineDisasterGuideListActivity.class);
                offlineCache.putExtra("userId", getIntent().getStringExtra("userId"));
                startActivity(offlineCache);
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Retrieve user data from the database
        retrieveUserData();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void retrieveUserData() {
        databaseReference.child(getIntent().getStringExtra("userId")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);

                    // Display username and email
                    View headerView = navigationView.getHeaderView(0);
                    TextView usernameTextView = headerView.findViewById(R.id.usernameTextView);
                    TextView emailTextView = headerView.findViewById(R.id.emailTextView);
                    usernameTextView.setText(username);
                    emailTextView.setText(email);
                } else {
                    Toast.makeText(HomeView.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeView.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar_menu, menu);
        return true;
    }

    // navigation drawer setup
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_profile:
                // Navigate to ProfileActivity
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra("userId", getIntent().getStringExtra("userId"));
                startActivity(intent);
                break;

            case R.id.action_notifications:
                Toast.makeText(getApplicationContext(), "Notifications selected", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_preparedness_guidelines:
                Intent offlineCache = new Intent(getApplicationContext(), OfflineDisasterGuideListActivity.class);
                offlineCache.putExtra("userId", getIntent().getStringExtra("userId"));
                startActivity(offlineCache);
                break;
        }

        return false;
    }

    // toolbar setup
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_profile:
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("userId", getIntent().getStringExtra("userId"));
                startActivity(intent);
                break;
        }
        return false;
    }
}
