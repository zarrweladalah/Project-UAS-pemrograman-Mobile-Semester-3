package com.example.todolist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.R;
import com.example.todolist.utils.SessionManager;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private static final long SPLASH_DELAY = 2500; // 2.5 seconds
    
    private Handler handler;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_splash);
            
            handler = new Handler(Looper.getMainLooper());
            sessionManager = new SessionManager(this);
            
            // Get views safely
            ImageView logoImage = findViewById(R.id.logo_image);
            ProgressBar progressBar = findViewById(R.id.progress_bar);
            TextView statusText = findViewById(R.id.status_text);
            
            // Show progress
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
            
            // Set status text
            if (statusText != null) {
                statusText.setText("Loading...");
            }
            
            // Simple animation for logo
            if (logoImage != null) {
                logoImage.setAlpha(0f);
                logoImage.animate()
                        .alpha(1f)
                        .setDuration(1000)
                        .start();
            }
            
            // Detect location from device locale (simple and safe)
            detectLocationFromLocale();
            
            // Navigate after delay
            handler.postDelayed(this::navigateToNextScreen, SPLASH_DELAY);
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            // If anything fails, just navigate
            navigateToNextScreen();
        }
    }
    
    private void detectLocationFromLocale() {
        try {
            // Get country from device locale - this is safe and won't crash
            String countryCode = Locale.getDefault().getCountry();
            
            if (countryCode == null || countryCode.isEmpty()) {
                countryCode = "ID"; // Default to Indonesia
            }
            
            String countryName = new Locale("", countryCode).getDisplayCountry();
            
            if (countryName == null || countryName.isEmpty()) {
                countryName = "Indonesia";
                countryCode = "ID";
            }
            
            Log.d(TAG, "Detected: " + countryName + " (" + countryCode + ")");
            
            // Save to session
            sessionManager.saveLocation(countryName, countryCode);
            
            // Update status text
            TextView statusText = findViewById(R.id.status_text);
            if (statusText != null) {
                statusText.setText("📍 " + countryName);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error detecting locale: " + e.getMessage());
            // Use default
            sessionManager.saveLocation("Indonesia", "ID");
        }
    }

    private void navigateToNextScreen() {
        try {
            // Remove any pending callbacks
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }
            
            Intent intent;
            if (sessionManager != null && sessionManager.isLoggedIn()) {
                intent = new Intent(this, MainActivity.class);
            } else {
                intent = new Intent(this, WelcomeActivity.class);
            }
            
            startActivity(intent);
            finish();
            
        } catch (Exception e) {
            Log.e(TAG, "Error navigating: " + e.getMessage());
            // Last resort - try to go to Welcome
            try {
                startActivity(new Intent(this, WelcomeActivity.class));
                finish();
            } catch (Exception e2) {
                Log.e(TAG, "Fatal error: " + e2.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy: " + e.getMessage());
        }
    }
}
