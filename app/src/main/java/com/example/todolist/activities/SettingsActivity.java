package com.example.todolist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.R;
import com.example.todolist.database.AppDatabase;
import com.example.todolist.database.TaskDao;
import com.example.todolist.utils.SessionManager;

public class SettingsActivity extends AppCompatActivity {
    private ImageButton backButton;
    private LinearLayout profileOption;
    private LinearLayout statisticsOption;
    private LinearLayout logoutOption;
    private TextView userNameText;
    private TextView userEmailText;
    private TextView taskStatsText;
    
    private SessionManager sessionManager;
    private TaskDao taskDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
        
        sessionManager = new SessionManager(this);
        taskDao = AppDatabase.getInstance(this).taskDao();

        loadUserInfo();
        setupClickListeners();
    }

    private void initViews() {
        backButton = findViewById(R.id.back_button);
        profileOption = findViewById(R.id.profile_option);
        statisticsOption = findViewById(R.id.statistics_option);
        logoutOption = findViewById(R.id.logout_option);
        userNameText = findViewById(R.id.user_name_text);
        userEmailText = findViewById(R.id.user_email_text);
        taskStatsText = findViewById(R.id.task_stats_text);
    }

    private void loadUserInfo() {
        String fullName = sessionManager.getFullName();
        String email = sessionManager.getEmail();
        
        userNameText.setText(fullName != null && !fullName.isEmpty() ? fullName : sessionManager.getUsername());
        userEmailText.setText(email);

        int userId = sessionManager.getUserId();
        int totalTasks = taskDao.getTaskCount(userId);
        int completedTasks = taskDao.getCompletedTaskCount(userId);
        
        taskStatsText.setText("📊 " + completedTasks + " of " + totalTasks + " tasks completed");
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        profileOption.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        statisticsOption.setOnClickListener(v -> {
            showStatisticsDialog();
        });

        logoutOption.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        sessionManager.logout();
                        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void showStatisticsDialog() {
        int userId = sessionManager.getUserId();
        int totalTasks = taskDao.getTaskCount(userId);
        int completedTasks = taskDao.getCompletedTaskCount(userId);
        int pendingTasks = totalTasks - completedTasks;
        
        double completionRate = totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0;

        String stats = "📋 Total Tasks: " + totalTasks + "\n" +
                      "✅ Completed: " + completedTasks + "\n" +
                      "⏳ Pending: " + pendingTasks + "\n" +
                      "📈 Completion Rate: " + String.format("%.1f", completionRate) + "%";

        new AlertDialog.Builder(this)
                .setTitle("📊 Your Statistics")
                .setMessage(stats)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserInfo();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

