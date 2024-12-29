package com.example.mynadma;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("message");

        Button buttonAddData = findViewById(R.id.buttonAddData);
        EditText editTextData = findViewById(R.id.editTextData);

        buttonAddData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text input from EditText
                String name = editTextData.getText().toString().trim();

                // Check if input is not empty
                if (!name.isEmpty()) {
                    // Push data to Firebase
                    ref.push().setValue(name, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError error, DatabaseReference ref) {
                            if (error == null) {
                                // Data added successfully
                                Toast.makeText(MainActivity.this, "Data added successfully!", Toast.LENGTH_SHORT).show();
                                editTextData.setText(""); // Clear the input field
                            } else {
                                // Handle the error
                                Toast.makeText(MainActivity.this, "Failed to add data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    // Notify user to enter some data
                    Toast.makeText(MainActivity.this, "Please enter some data.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}