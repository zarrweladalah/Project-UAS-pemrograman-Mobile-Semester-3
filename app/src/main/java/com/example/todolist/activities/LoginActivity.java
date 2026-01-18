package com.example.todolist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private TextView registerLink;
    private TextView forgotPasswordLink;
    
    private UserDao userDao;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        
        userDao = AppDatabase.getInstance(this).userDao();
        sessionManager = new SessionManager(this);

        loginButton.setOnClickListener(v -> attemptLogin());
        
        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        forgotPasswordLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void initViews() {
        emailInputLayout = findViewById(R.id.email_input_layout);
        passwordInputLayout = findViewById(R.id.password_input_layout);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        registerLink = findViewById(R.id.register_link);
        forgotPasswordLink = findViewById(R.id.forgot_password_link);
    }

    private void attemptLogin() {
        emailInputLayout.setError(null);
        passwordInputLayout.setError(null);

        String emailOrUsername = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        boolean hasError = false;

        if (TextUtils.isEmpty(emailOrUsername)) {
            emailInputLayout.setError("Email or username is required");
            hasError = true;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInputLayout.setError("Password is required");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        User user = userDao.login(emailOrUsername, password);

        if (user != null) {
            sessionManager.createLoginSession(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFullName() != null ? user.getFullName() : user.getUsername()
            );
            
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        } else {
            Toast.makeText(this, "Invalid email/username or password", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

