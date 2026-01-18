package com.example.todolist.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.R;
import com.example.todolist.utils.GreetingHelper;
import com.example.todolist.utils.SessionManager;

public class WelcomeActivity extends AppCompatActivity {
    private ImageView welcomeImage;
    private TextView greetingText;
    private TextView welcomeText;
    private TextView locationText;
    private Button continueButton;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        welcomeImage = findViewById(R.id.welcome_image);
        greetingText = findViewById(R.id.greeting_text);
        welcomeText = findViewById(R.id.welcome_text);
        locationText = findViewById(R.id.location_text);
        continueButton = findViewById(R.id.continue_button);

        sessionManager = new SessionManager(this);

        setupContent();
        startAnimations();

        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });
    }

    private void setupContent() {
        String countryCode = sessionManager.getCountryCode();
        String location = sessionManager.getLocation();

        String greeting = GreetingHelper.getTimeBasedGreeting(countryCode);
        String welcome = GreetingHelper.getWelcomeText(countryCode);
        String buttonText = GreetingHelper.getContinueButtonText(countryCode);

        greetingText.setText(greeting);
        welcomeText.setText(welcome);
        locationText.setText("📍 " + location);
        continueButton.setText(buttonText);
    }

    private void startAnimations() {
        // Initial states
        welcomeImage.setAlpha(0f);
        welcomeImage.setTranslationY(-50f);
        
        greetingText.setAlpha(0f);
        greetingText.setTranslationY(30f);
        
        welcomeText.setAlpha(0f);
        welcomeText.setTranslationY(30f);
        
        locationText.setAlpha(0f);
        locationText.setTranslationY(30f);
        
        continueButton.setAlpha(0f);
        continueButton.setScaleX(0.8f);
        continueButton.setScaleY(0.8f);

        // Image animation
        ObjectAnimator imgFade = ObjectAnimator.ofFloat(welcomeImage, "alpha", 0f, 1f);
        ObjectAnimator imgTranslate = ObjectAnimator.ofFloat(welcomeImage, "translationY", -50f, 0f);
        AnimatorSet imgAnim = new AnimatorSet();
        imgAnim.playTogether(imgFade, imgTranslate);
        imgAnim.setDuration(600);
        imgAnim.setInterpolator(new AccelerateDecelerateInterpolator());

        // Greeting animation
        ObjectAnimator greetFade = ObjectAnimator.ofFloat(greetingText, "alpha", 0f, 1f);
        ObjectAnimator greetTranslate = ObjectAnimator.ofFloat(greetingText, "translationY", 30f, 0f);
        AnimatorSet greetAnim = new AnimatorSet();
        greetAnim.playTogether(greetFade, greetTranslate);
        greetAnim.setDuration(500);
        greetAnim.setStartDelay(300);

        // Welcome animation
        ObjectAnimator welcomeFade = ObjectAnimator.ofFloat(welcomeText, "alpha", 0f, 1f);
        ObjectAnimator welcomeTranslate = ObjectAnimator.ofFloat(welcomeText, "translationY", 30f, 0f);
        AnimatorSet welcomeAnim = new AnimatorSet();
        welcomeAnim.playTogether(welcomeFade, welcomeTranslate);
        welcomeAnim.setDuration(500);
        welcomeAnim.setStartDelay(500);

        // Location animation
        ObjectAnimator locFade = ObjectAnimator.ofFloat(locationText, "alpha", 0f, 1f);
        ObjectAnimator locTranslate = ObjectAnimator.ofFloat(locationText, "translationY", 30f, 0f);
        AnimatorSet locAnim = new AnimatorSet();
        locAnim.playTogether(locFade, locTranslate);
        locAnim.setDuration(500);
        locAnim.setStartDelay(700);

        // Button animation
        ObjectAnimator btnFade = ObjectAnimator.ofFloat(continueButton, "alpha", 0f, 1f);
        ObjectAnimator btnScaleX = ObjectAnimator.ofFloat(continueButton, "scaleX", 0.8f, 1f);
        ObjectAnimator btnScaleY = ObjectAnimator.ofFloat(continueButton, "scaleY", 0.8f, 1f);
        AnimatorSet btnAnim = new AnimatorSet();
        btnAnim.playTogether(btnFade, btnScaleX, btnScaleY);
        btnAnim.setDuration(500);
        btnAnim.setStartDelay(900);
        btnAnim.setInterpolator(new OvershootInterpolator());

        // Start all animations
        imgAnim.start();
        greetAnim.start();
        welcomeAnim.start();
        locAnim.start();
        btnAnim.start();
    }
}

