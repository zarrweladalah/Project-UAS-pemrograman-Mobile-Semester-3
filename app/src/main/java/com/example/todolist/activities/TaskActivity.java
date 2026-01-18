package com.example.todolist.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.R;
import com.example.todolist.database.AppDatabase;
import com.example.todolist.database.FolderDao;
import com.example.todolist.database.TaskDao;
import com.example.todolist.models.Folder;
import com.example.todolist.models.Task;
import com.example.todolist.utils.SessionManager;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskActivity extends AppCompatActivity {
    private ImageButton backButton;
    private ImageButton deleteButton;
    private TextInputLayout titleInputLayout;
    private TextInputLayout descriptionInputLayout;
    private EditText titleInput;
    private EditText descriptionInput;
    private RadioGroup priorityGroup;
    private RadioButton priorityLow;
    private RadioButton priorityMedium;
    private RadioButton priorityHigh;
    private CheckBox completedCheckbox;
    private Button folderButton;
    private Button saveButton;
    private TextView createdDateText;
    
    private TaskDao taskDao;
    private FolderDao folderDao;
    private SessionManager sessionManager;
    
    private Task currentTask;
    private int taskId = -1;
    private Integer selectedFolderId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        initViews();
        
        AppDatabase database = AppDatabase.getInstance(this);
        taskDao = database.taskDao();
        folderDao = database.folderDao();
        sessionManager = new SessionManager(this);

        taskId = getIntent().getIntExtra("task_id", -1);

        if (taskId != -1) {
            loadTask();
        } else {
            createdDateText.setText("New Task");
        }

        setupClickListeners();
    }

    private void initViews() {
        backButton = findViewById(R.id.back_button);
        deleteButton = findViewById(R.id.delete_button);
        titleInputLayout = findViewById(R.id.title_input_layout);
        descriptionInputLayout = findViewById(R.id.description_input_layout);
        titleInput = findViewById(R.id.title_input);
        descriptionInput = findViewById(R.id.description_input);
        priorityGroup = findViewById(R.id.priority_group);
        priorityLow = findViewById(R.id.priority_low);
        priorityMedium = findViewById(R.id.priority_medium);
        priorityHigh = findViewById(R.id.priority_high);
        completedCheckbox = findViewById(R.id.completed_checkbox);
        folderButton = findViewById(R.id.folder_button);
        saveButton = findViewById(R.id.save_button);
        createdDateText = findViewById(R.id.created_date_text);
    }

    private void loadTask() {
        currentTask = taskDao.getTaskById(taskId);
        
        if (currentTask != null) {
            titleInput.setText(currentTask.getTitle());
            descriptionInput.setText(currentTask.getDescription());
            completedCheckbox.setChecked(currentTask.isCompleted());
            selectedFolderId = currentTask.getFolderId();

            // Set priority
            switch (currentTask.getPriority()) {
                case 1:
                    priorityLow.setChecked(true);
                    break;
                case 2:
                    priorityMedium.setChecked(true);
                    break;
                case 3:
                    priorityHigh.setChecked(true);
                    break;
            }

            // Set folder button text
            if (selectedFolderId != null) {
                Folder folder = folderDao.getFolderById(selectedFolderId);
                if (folder != null) {
                    folderButton.setText("📁 " + folder.getName());
                }
            }

            // Set created date
            SimpleDateFormat sdf = new SimpleDateFormat("Created: MMM dd, yyyy HH:mm", Locale.getDefault());
            createdDateText.setText(sdf.format(new Date(currentTask.getCreatedAt())));

            deleteButton.setVisibility(View.VISIBLE);
        }
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        if (currentTask != null) {
                            taskDao.delete(currentTask);
                            Toast.makeText(this, "Task deleted!", Toast.LENGTH_SHORT).show();
                            finish();
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        folderButton.setOnClickListener(v -> showFolderPicker());

        saveButton.setOnClickListener(v -> saveTask());
    }

    private void showFolderPicker() {
        List<Folder> folders = folderDao.getFoldersByUserId(sessionManager.getUserId());
        
        String[] folderNames = new String[folders.size() + 1];
        folderNames[0] = "No Folder";
        for (int i = 0; i < folders.size(); i++) {
            folderNames[i + 1] = folders.get(i).getName();
        }

        int checkedItem = 0;
        if (selectedFolderId != null) {
            for (int i = 0; i < folders.size(); i++) {
                if (folders.get(i).getId() == selectedFolderId) {
                    checkedItem = i + 1;
                    break;
                }
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Select Folder")
                .setSingleChoiceItems(folderNames, checkedItem, (dialog, which) -> {
                    if (which == 0) {
                        selectedFolderId = null;
                        folderButton.setText("📁 Select Folder");
                    } else {
                        selectedFolderId = folders.get(which - 1).getId();
                        folderButton.setText("📁 " + folders.get(which - 1).getName());
                    }
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveTask() {
        titleInputLayout.setError(null);

        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        boolean isCompleted = completedCheckbox.isChecked();

        if (TextUtils.isEmpty(title)) {
            titleInputLayout.setError("Title is required");
            return;
        }

        // Get priority
        int priority = 2; // Default medium
        if (priorityLow.isChecked()) {
            priority = 1;
        } else if (priorityHigh.isChecked()) {
            priority = 3;
        }

        if (currentTask != null) {
            // Update existing task
            currentTask.setTitle(title);
            currentTask.setDescription(description);
            currentTask.setCompleted(isCompleted);
            currentTask.setPriority(priority);
            currentTask.setFolderId(selectedFolderId);
            currentTask.setUpdatedAt(System.currentTimeMillis());
            
            taskDao.update(currentTask);
            Toast.makeText(this, "Task updated!", Toast.LENGTH_SHORT).show();
        } else {
            // Create new task
            Task newTask = new Task(title, sessionManager.getUserId());
            newTask.setDescription(description);
            newTask.setCompleted(isCompleted);
            newTask.setPriority(priority);
            newTask.setFolderId(selectedFolderId);
            
            taskDao.insert(newTask);
            Toast.makeText(this, "Task created!", Toast.LENGTH_SHORT).show();
        }

        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

