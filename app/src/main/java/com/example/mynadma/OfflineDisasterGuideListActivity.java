// shows a list of offline card
// when user click on it, the app show the disaster information
package com.example.mynadma;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OfflineDisasterGuideListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private List<OfflineDisasterGuideCard> cardItemList;

    private DatabaseReference ref;
    private NavigationView navigationView;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting the disaster reference
        ref = FirebaseDatabase.getInstance().getReference("disasterGuide");

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_offline_guide_list);

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

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // open the nadma portal for more guidelines
        TextView hyperlinkText = findViewById(R.id.hyperlinkText);
        hyperlinkText.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://www.nadma.gov.my/bm/media-2/infografik-dan-poster"));
            startActivity(intent);
        });

        // using geocoder to get the location data of the user
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // If permission is granted, get the location
            getLastLocation();
        }

        getDisasterInformation();
        retrieveUserData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_notifications) {
            Toast.makeText(this, "Notifications selected", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.action_profile) {
            // Navigate to ProfileActivity
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("userId", getIntent().getStringExtra("userId"));
            startActivity(intent);
        }

        return false;
    }

    private void getDisasterInformation() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<OfflineGuide> guideList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    OfflineGuide offlineGuide = snapshot.getValue(OfflineGuide.class);
                    offlineGuide.setKey(snapshot.getKey());
                    guideList.add(offlineGuide);
                }

                if(guideList.isEmpty()) {
                    Toast.makeText(OfflineDisasterGuideListActivity.this, "No new disaster information found", Toast.LENGTH_SHORT).show();
                } else {
                    cardItemList = new ArrayList<>();
                    for(OfflineGuide offlineGuide: guideList) {
                        String title = offlineGuide.getTitle();
                        OfflineDisasterGuideCard newCard = new OfflineDisasterGuideCard(title, "01/01/2025");
                        newCard.setdatabaseKey(offlineGuide.getKey());
                        cardItemList.add(newCard);

                    }

                    String userId = getIntent().getStringExtra("userId");
                    cardAdapter = new CardAdapter(cardItemList, userId);
                    recyclerView.setAdapter(cardAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error: " + error.getMessage());
            }
        });
    }

    private void retrieveUserData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(getIntent().getStringExtra("userId")).addListenerForSingleValueEvent(new ValueEventListener() {
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OfflineDisasterGuideListActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            getCityAndState(latitude, longitude);
                        } else {
                            Toast.makeText(OfflineDisasterGuideListActivity.this,
                                    "Unable to get location. Make sure location is enabled on the device.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getCityAndState(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String state = addresses.get(0).getAdminArea();   // State name
                String city = addresses.get(0).getLocality();      // City name
                setStatusBar(city, state);
            } else {
                Toast.makeText(this,
                        "Unable to get address from location.",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                setStatusBar(null, null);
            }
        }
    }

    // set the color and the text of the status bar
    private void setStatusBar(String city, String state) {
        // set the status bar message
        if(city != null && state != null) {
            getConnectionRating(city, state);
        } else {
            LinearLayout userLocationStatusBar = (LinearLayout) findViewById(R.id.statusBar);
            TextView locationText = (TextView) findViewById(R.id.locationText);
            TextView downloadAdvice = (TextView) findViewById(R.id.downloadAdvice);
            int color = ContextCompat.getColor(getApplicationContext(), R.color.badConnection);
            String advice = ContextCompat.getString(getApplicationContext(), R.string.unknownConnection);;

            userLocationStatusBar.setBackgroundColor(color);
            locationText.setText("Your location cannot be determined!");
            downloadAdvice.setText(advice);
        }
    }
    
    private void getConnectionRating(String city, String state) {
        String collectionReference = "networkRating/" + state.toLowerCase() + "/" + city.toLowerCase();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(collectionReference);
        LinearLayout userLocationStatusBar = (LinearLayout) findViewById(R.id.statusBar);
        TextView locationText = (TextView) findViewById(R.id.locationText);
        TextView downloadAdvice = (TextView) findViewById(R.id.downloadAdvice);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Assuming the connection rating is stored as an integer (1 to 5)
                Integer connectionRating = dataSnapshot.getValue(Integer.class);
                int color = 0;
                String advice = "";
                String userLocation = city + ", " + state;
                locationText.setText(userLocation);

                // Set the background color based on the connection rating
                if (connectionRating != null) {
                    if (connectionRating == 1 || connectionRating == 2) {
                        // Low connection (Red)
                        color = ContextCompat.getColor(getApplicationContext(), R.color.badConnection);
                        advice = ContextCompat.getString(getApplicationContext(), R.string.badConnection);
                    } else if (connectionRating == 3) {
                        // Neutral connection (Yellow)
                        color = ContextCompat.getColor(getApplicationContext(), R.color.neutralConnection);
                        advice = ContextCompat.getString(getApplicationContext(), R.string.neutralConnection);
                    } else if (connectionRating == 4 || connectionRating == 5) {
                        // High connection (Green)
                        color = ContextCompat.getColor(getApplicationContext(), R.color.goodConnection);
                        advice = ContextCompat.getString(getApplicationContext(), R.string.goodConnection);
                    }

                } else {
                    color = ContextCompat.getColor(getApplicationContext(), R.color.badConnection);
                    advice = ContextCompat.getString(getApplicationContext(), R.string.unknownConnection);;
                }

                // Set the background color of the status bar and the advice text
                userLocationStatusBar.setBackgroundColor(color);
                downloadAdvice.setText(advice);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors (e.g., database read failure)
                Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
}