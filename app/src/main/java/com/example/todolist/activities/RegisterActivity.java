package com.example.todolist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.R;
import com.example.todolist.database.AppDatabase;
import com.example.todolist.database.UserDao;
import com.example.todolist.models.User;
import com.example.todolist.utils.SessionManager;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout usernameInputLayout;
    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private TextInputLayout confirmPasswordInputLayout;
    private EditText usernameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private Button registerButton;
    private TextView loginLink;
    
    private UserDao userDao;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        
        userDao = AppDatabase.getInstance(this).userDao();
        sessionManager = new SessionManager(this);

        registerButton.setOnClickListener(v -> attemptRegister());
        
        loginLink.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    private void initViews() {
        usernameInputLayout = findViewById(R.id.username_input_layout);
        emailInputLayout = findViewById(R.id.email_input_layout);
        passwordInputLayout = findViewById(R.id.password_input_layout);
        confirmPasswordInputLayout = findViewById(R.id.confirm_password_input_layout);
        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        registerButton = findViewById(R.id.register_button);
        loginLink = findViewById(R.id.login_link);
    }

    private void attemptRegister() {
        // Clear errors
        usernameInputLayout.setError(null);
        emailInputLayout.setError(null);
        passwordInputLayout.setError(null);
        confirmPasswordInputLayout.setError(null);

        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        boolean hasError = false;

        if (TextUtils.isEmpty(username)) {
            usernameInputLayout.setError("Username is required");
            hasError = true;
        } else if (username.length() < 3) {
            usernameInputLayout.setError("Username must be at least 3 characters");
            hasError = true;
        }

        if (TextUtils.isEmpty(email)) {
            emailInputLayout.setError("Email is required");
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.setError("Please enter a valid email");
            hasError = true;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInputLayout.setError("Password is required");
            hasError = true;
        } else if (password.length() < 6) {
            passwordInputLayout.setError("Password must be at least 6 characters");
            hasError = true;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordInputLayout.setError("Please confirm your password");
            hasError = true;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordInputLayout.setError("Passwords do not match");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        // Check if user already exists
        User existingUser = userDao.checkUserExists(email, username);
        if (existingUser != null) {
            if (existingUser.getEmail().equals(email)) {
                emailInputLayout.setError("Email already registered");
            }
            if (existingUser.getUsername().equals(username)) {
                usernameInputLayout.setError("Username already taken");
            }
            return;
        }

        // Create new user
        User newUser = new User(username, email, password);
        long userId = userDao.insert(newUser);

        if (userId > 0) {
            sessionManager.createLoginSession(
                    (int) userId,
                    username,
                    email,
                    username
            );
            
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        } else {
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

