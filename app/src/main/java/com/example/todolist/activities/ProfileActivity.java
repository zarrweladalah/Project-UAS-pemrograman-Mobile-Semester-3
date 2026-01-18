package com.example.todolist.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.R;
import com.example.todolist.database.AppDatabase;
import com.example.todolist.database.UserDao;
import com.example.todolist.models.User;
import com.example.todolist.utils.SessionManager;
import com.google.android.material.textfield.TextInputLayout;

public class ProfileActivity extends AppCompatActivity {
    private ImageButton backButton;
    private TextInputLayout fullNameInputLayout;
    private TextInputLayout emailInputLayout;
    private TextInputLayout phoneInputLayout;
    private TextInputLayout currentPasswordInputLayout;
    private TextInputLayout newPasswordInputLayout;
    private EditText fullNameInput;
    private EditText emailInput;
    private EditText phoneInput;
    private EditText currentPasswordInput;
    private EditText newPasswordInput;
    private Button saveProfileButton;
    private Button changePasswordButton;
    
    private UserDao userDao;
    private SessionManager sessionManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        
        userDao = AppDatabase.getInstance(this).userDao();
        sessionManager = new SessionManager(this);

        loadUserProfile();
        setupClickListeners();
    }

    private void initViews() {
        backButton = findViewById(R.id.back_button);
        fullNameInputLayout = findViewById(R.id.full_name_input_layout);
        emailInputLayout = findViewById(R.id.email_input_layout);
        phoneInputLayout = findViewById(R.id.phone_input_layout);
        currentPasswordInputLayout = findViewById(R.id.current_password_input_layout);
        newPasswordInputLayout = findViewById(R.id.new_password_input_layout);
        fullNameInput = findViewById(R.id.full_name_input);
        emailInput = findViewById(R.id.email_input);
        phoneInput = findViewById(R.id.phone_input);
        currentPasswordInput = findViewById(R.id.current_password_input);
        newPasswordInput = findViewById(R.id.new_password_input);
        saveProfileButton = findViewById(R.id.save_profile_button);
        changePasswordButton = findViewById(R.id.change_password_button);
    }

    private void loadUserProfile() {
        int userId = sessionManager.getUserId();
        currentUser = userDao.getUserById(userId);

        if (currentUser != null) {
            fullNameInput.setText(currentUser.getFullName());
            emailInput.setText(currentUser.getEmail());
            phoneInput.setText(currentUser.getPhoneNumber());
        }
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        saveProfileButton.setOnClickListener(v -> saveProfile());
        changePasswordButton.setOnClickListener(v -> changePassword());
    }

    private void saveProfile() {
        fullNameInputLayout.setError(null);
        emailInputLayout.setError(null);

        String fullName = fullNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();

        boolean hasError = false;

        if (TextUtils.isEmpty(fullName)) {
            fullNameInputLayout.setError("Full name is required");
            hasError = true;
        }

        if (TextUtils.isEmpty(email)) {
            emailInputLayout.setError("Email is required");
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.setError("Please enter a valid email");
            hasError = true;
        }

        // Check if email is already taken by another user
        if (!hasError && !email.equals(currentUser.getEmail())) {
            User existingUser = userDao.getUserByEmail(email);
            if (existingUser != null && existingUser.getId() != currentUser.getId()) {
                emailInputLayout.setError("Email already registered");
                hasError = true;
            }
        }

        if (hasError) {
            return;
        }

        currentUser.setFullName(fullName);
        currentUser.setEmail(email);
        currentUser.setPhoneNumber(phone.isEmpty() ? null : phone);
        currentUser.setUpdatedAt(System.currentTimeMillis());

        userDao.update(currentUser);
        sessionManager.updateProfile(fullName, email);

        Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
    }

    private void changePassword() {
        currentPasswordInputLayout.setError(null);
        newPasswordInputLayout.setError(null);

        String currentPassword = currentPasswordInput.getText().toString().trim();
        String newPassword = newPasswordInput.getText().toString().trim();

        boolean hasError = false;

        if (TextUtils.isEmpty(currentPassword)) {
            currentPasswordInputLayout.setError("Current password is required");
            hasError = true;
        } else if (!currentPassword.equals(currentUser.getPassword())) {
            currentPasswordInputLayout.setError("Incorrect current password");
            hasError = true;
        }

        if (TextUtils.isEmpty(newPassword)) {
            newPasswordInputLayout.setError("New password is required");
            hasError = true;
        } else if (newPassword.length() < 6) {
            newPasswordInputLayout.setError("Password must be at least 6 characters");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        currentUser.setPassword(newPassword);
        currentUser.setUpdatedAt(System.currentTimeMillis());
        userDao.update(currentUser);

        currentPasswordInput.setText("");
        newPasswordInput.setText("");

        Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

