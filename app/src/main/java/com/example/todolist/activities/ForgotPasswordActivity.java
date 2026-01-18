package com.example.todolist.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.R;
import com.example.todolist.database.AppDatabase;
import com.example.todolist.database.UserDao;
import com.example.todolist.models.User;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordActivity extends AppCompatActivity {
    private TextInputLayout emailInputLayout;
    private TextInputLayout newPasswordInputLayout;
    private TextInputLayout confirmPasswordInputLayout;
    private EditText emailInput;
    private EditText newPasswordInput;
    private EditText confirmPasswordInput;
    private Button verifyButton;
    private Button resetButton;
    private LinearLayout passwordResetLayout;
    private TextView backToLoginLink;
    
    private UserDao userDao;
    private String verifiedEmail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();
        
        userDao = AppDatabase.getInstance(this).userDao();

        verifyButton.setOnClickListener(v -> verifyEmail());
        resetButton.setOnClickListener(v -> resetPassword());
        
        backToLoginLink.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    private void initViews() {
        emailInputLayout = findViewById(R.id.email_input_layout);
        newPasswordInputLayout = findViewById(R.id.new_password_input_layout);
        confirmPasswordInputLayout = findViewById(R.id.confirm_password_input_layout);
        emailInput = findViewById(R.id.email_input);
        newPasswordInput = findViewById(R.id.new_password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        verifyButton = findViewById(R.id.verify_button);
        resetButton = findViewById(R.id.reset_button);
        passwordResetLayout = findViewById(R.id.password_reset_layout);
        backToLoginLink = findViewById(R.id.back_to_login_link);
    }

    private void verifyEmail() {
        emailInputLayout.setError(null);

        String email = emailInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailInputLayout.setError("Email is required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.setError("Please enter a valid email");
            return;
        }

        User user = userDao.getUserByEmail(email);

        if (user != null) {
            verifiedEmail = email;
            emailInput.setEnabled(false);
            verifyButton.setVisibility(View.GONE);
            passwordResetLayout.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Email verified! Please enter your new password.", Toast.LENGTH_SHORT).show();
        } else {
            emailInputLayout.setError("No account found with this email");
        }
    }

    private void resetPassword() {
        newPasswordInputLayout.setError(null);
        confirmPasswordInputLayout.setError(null);

        String newPassword = newPasswordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        boolean hasError = false;

        if (TextUtils.isEmpty(newPassword)) {
            newPasswordInputLayout.setError("New password is required");
            hasError = true;
        } else if (newPassword.length() < 6) {
            newPasswordInputLayout.setError("Password must be at least 6 characters");
            hasError = true;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordInputLayout.setError("Please confirm your password");
            hasError = true;
        } else if (!newPassword.equals(confirmPassword)) {
            confirmPasswordInputLayout.setError("Passwords do not match");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        if (verifiedEmail != null) {
            userDao.updatePassword(verifiedEmail, newPassword);
            Toast.makeText(this, "Password reset successful! Please login with your new password.", Toast.LENGTH_LONG).show();
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

