package com.example.mynadma;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private Button logoutButton;
    private TextView changePasswordLink;
    private Button deleteAccountButton;
    private TextView contactSupportLink; // New TextView for "Contact Support"
    private DatabaseReference databaseReference;
    private RecyclerView profileRecyclerView;
    private ProfileAdapter profileAdapter;
    private List<Profile> profileList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize DrawerLayout
        DrawerLayout drawerLayout = findViewById(R.id.drawer);

        // Handle NavigationIcon click
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Initialize NavigationView
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0); // Access the drawer header layout
        TextView drawerUsername = headerView.findViewById(R.id.usernameTextView);
        TextView drawerEmail = headerView.findViewById(R.id.emailTextView);

        // Initialize TextView and Button references
        welcomeTextView = findViewById(R.id.userwelcomeTextView);
        logoutButton = findViewById(R.id.buttonLogin);
        changePasswordLink = findViewById(R.id.signupLink);
        deleteAccountButton = findViewById(R.id.buttonDelete);
        contactSupportLink = findViewById(R.id.contactSupportLink); // Initialize "Contact Support" link

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize RecyclerView and Adapter
        profileRecyclerView = findViewById(R.id.profileRecyclerView);
        profileRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        profileList = new ArrayList<>();
        profileAdapter = new ProfileAdapter(profileList);
        profileRecyclerView.setAdapter(profileAdapter);

        // Get the userId passed from the login activity
        String userId = getIntent().getStringExtra("userId");
        if (userId != null) {
            // Retrieve user data from Firebase
            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Extract data from the snapshot
                        String username = snapshot.child("username").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String contact = snapshot.child("contact").getValue(String.class);

                        // Set data to TextViews
                        if (username != null) {
                            welcomeTextView.setText("Welcome, " + username);
                            drawerUsername.setText(username); // Set username in drawer
                        }
                        if (email != null) {
                            drawerEmail.setText(email); // Set email in drawer
                        }

                        // Add profile data to RecyclerView
                        if (username != null && email != null && contact != null) {
                            profileList.clear(); // Clear the list before adding new data
                            profileList.add(new Profile("Username", username));
                            profileList.add(new Profile("Email", email));
                            profileList.add(new Profile("Contact", contact));
                            profileAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProfileActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "UserId is null", Toast.LENGTH_SHORT).show();
        }

        // Set logout button click listener
        logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());

        // Set click listener for "Change Password"
        changePasswordLink.setOnClickListener(v -> navigateToFingerprintActivity());

        // Set click listener for "Delete Account"
        deleteAccountButton.setOnClickListener(v -> showDeleteAccountConfirmationDialog());

        // Set click listener for "Contact Support"
        contactSupportLink.setOnClickListener(v -> contactSupport());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_profile) {
            // Refresh the current activity
            recreate();
            return true;
        } else if (itemId == R.id.action_notifications) {
            Toast.makeText(this, "Notifications selected", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Logout", (dialog, which) -> navigateToIntroductionScreen())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showDeleteAccountConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Account Deletion")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Delete", (dialog, which) -> deleteAccount())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteAccount() {
        String userId = getIntent().getStringExtra("userId");
        if (userId != null) {
            databaseReference.child(userId).removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                            navigateToIntroductionScreen();  // Redirect to the introduction screen after deletion
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToIntroductionScreen() {
        Intent intent = new Intent(ProfileActivity.this, IntroductionScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToFingerprintActivity() {
        // Retrieve userId from the intent
        String userId = getIntent().getStringExtra("userId"); // Retrieve userId from Intent
        if (userId != null) {
            Intent intent = new Intent(ProfileActivity.this, FingerprintActivity.class);
            intent.putExtra("userId", userId); // Pass userId to FingerprintActivity
            startActivity(intent);
        } else {
            Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void contactSupport() {
        // Implicit intent to dial phone number
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:0380642400"));
        startActivity(intent);
    }
}
