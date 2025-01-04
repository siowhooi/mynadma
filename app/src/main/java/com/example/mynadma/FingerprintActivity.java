package com.example.mynadma;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class FingerprintActivity extends AppCompatActivity {

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);

        // Retrieve userId from the intent
        userId = getIntent().getStringExtra("userId");

        // Authenticate the user using biometric authentication
        authenticateBiometric();
    }

    private void authenticateBiometric() {
        // Create a BiometricPrompt to handle the biometric authentication
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(FingerprintActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                // Navigate to ChangePasswordActivity with userId on successful biometric detection
                navigateToChangePassword();
            }

            @Override
            public void onAuthenticationFailed() {
                Toast.makeText(FingerprintActivity.this, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                Toast.makeText(FingerprintActivity.this, "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }
        });

        // Configure biometric prompt
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Use your fingerprint to authenticate")
                .setDescription("Confirm your identity to proceed")
                .setNegativeButtonText("Cancel")
                .build();

        // Start biometric prompt
        biometricPrompt.authenticate(promptInfo);
    }

    private void navigateToChangePassword() {
        Intent intent = new Intent(FingerprintActivity.this, ChangePasswordActivity.class);
        intent.putExtra("userId", userId); // Passing userId to ChangePasswordActivity
        startActivity(intent);
    }
}
