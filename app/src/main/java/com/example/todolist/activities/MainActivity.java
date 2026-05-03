package com.example.todolist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.adapters.FolderAdapter;
import com.example.todolist.adapters.TaskAdapter;
import com.example.todolist.database.AppDatabase;
import com.example.todolist.database.FolderDao;
import com.example.todolist.database.TaskDao;
import com.example.todolist.models.Folder;
import com.example.todolist.models.Task;
import com.example.todolist.utils.SessionManager;
import com.example.todolist.utils.AiHelper; // Import AI Helper
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener, FolderAdapter.OnFolderClickListener {
    private RecyclerView tasksRecyclerView;
    private RecyclerView foldersRecyclerView;
    private FloatingActionButton fabAddTask;
    private FloatingActionButton fabAddFolder;
    private ImageButton settingsButton;
    private TextView welcomeText;
    private TextView taskCountText;
    private LinearLayout emptyStateLayout;

    private TaskAdapter taskAdapter;
    private FolderAdapter folderAdapter;
    private TaskDao taskDao;
    private FolderDao folderDao;
    private SessionManager sessionManager;

    private List<Task> taskList = new ArrayList<>();
    private List<Folder> folderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        AppDatabase database = AppDatabase.getInstance(this);
        taskDao = database.taskDao();
        folderDao = database.folderDao();
        sessionManager = new SessionManager(this);

        setupRecyclerViews();
        setupClickListeners();
        updateWelcomeText();
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void initViews() {
        tasksRecyclerView = findViewById(R.id.tasks_recycler_view);
        foldersRecyclerView = findViewById(R.id.folders_recycler_view);
        fabAddTask = findViewById(R.id.fab_add_task);
        fabAddFolder = findViewById(R.id.fab_add_folder);
        settingsButton = findViewById(R.id.settings_button);
        welcomeText = findViewById(R.id.welcome_text);
        taskCountText = findViewById(R.id.task_count_text);
        emptyStateLayout = findViewById(R.id.empty_state_layout);
    }

    private void setupRecyclerViews() {
        taskAdapter = new TaskAdapter(taskList, this);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksRecyclerView.setAdapter(taskAdapter);

        folderAdapter = new FolderAdapter(folderList, this, taskDao);
        foldersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        foldersRecyclerView.setAdapter(folderAdapter);
    }

    private void setupClickListeners() {
        fabAddTask.setOnClickListener(v -> showAddTaskDialog(null));
        fabAddFolder.setOnClickListener(v -> showAddFolderDialog());
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void updateWelcomeText() {
        String fullName = sessionManager.getFullName();
        if (TextUtils.isEmpty(fullName)) {
            fullName = sessionManager.getUsername();
        }
        welcomeText.setText("Hello, " + fullName + "! 👋");
    }

    private void loadData() {
        int userId = sessionManager.getUserId();
        folderList.clear();
        folderList.addAll(folderDao.getFoldersByUserId(userId));
        folderAdapter.updateFolders(folderList);

        taskList.clear();
        taskList.addAll(taskDao.getTasksWithoutFolder(userId));
        taskAdapter.updateTasks(taskList);

        int totalTasks = taskDao.getTaskCount(userId);
        int completedTasks = taskDao.getCompletedTaskCount(userId);
        taskCountText.setText(completedTasks + "/" + totalTasks + " tasks completed");

        if (taskList.isEmpty() && folderList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
        }
    }

    // --- BAGIAN YANG DIEDIT UNTUK AI ---
    private void showAddTaskDialog(Integer folderId) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        EditText titleInput = dialogView.findViewById(R.id.task_title_input);
        EditText descriptionInput = dialogView.findViewById(R.id.task_description_input);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add New Task (AI Enhanced)")
                .setView(dialogView)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                String title = titleInput.getText().toString().trim();
                String description = descriptionInput.getText().toString().trim();

                if (TextUtils.isEmpty(title)) {
                    titleInput.setError("Title is required");
                    return;
                }

                // Efek loading biar kelihatan AI-nya kerja
                positiveButton.setEnabled(false);
                positiveButton.setText("AI Thinking...");

                // Panggil AI untuk tentuin prioritas
                AiHelper.detectPriority(title, new AiHelper.AiPriorityCallback() {
                    @Override
                    public void onResult(int aiPriority) {
                        runOnUiThread(() -> {
                            Task newTask = new Task(title, sessionManager.getUserId());
                            newTask.setDescription(description);
                            newTask.setFolderId(folderId);
                            newTask.setPriority(aiPriority); // Set hasil AI

                            taskDao.insert(newTask);
                            Toast.makeText(MainActivity.this, "AI set priority to: " + aiPriority, Toast.LENGTH_SHORT).show();
                            loadData();
                            dialog.dismiss();
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            // Kalau error, simpan dengan prioritas default (2)
                            Task newTask = new Task(title, sessionManager.getUserId());
                            newTask.setDescription(description);
                            newTask.setFolderId(folderId);
                            newTask.setPriority(2);

                            taskDao.insert(newTask);
                            loadData();
                            dialog.dismiss();
                        });
                    }
                });
            });
        });

        dialog.show();
    }

    private void showAddFolderDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_folder, null);
        EditText nameInput = dialogView.findViewById(R.id.folder_name_input);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add New Folder")
                .setView(dialogView)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                String name = nameInput.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    nameInput.setError("Folder name is required");
                    return;
                }
                Folder newFolder = new Folder(name, sessionManager.getUserId());
                folderDao.insert(newFolder);
                loadData();
                dialog.dismiss();
            });
        });
        dialog.show();
    }

    @Override
    public void onTaskClick(Task task) {
        Intent intent = new Intent(this, TaskActivity.class);
        intent.putExtra("task_id", task.getId());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onTaskCheckChanged(Task task, boolean isCompleted) {
        task.setCompleted(isCompleted);
        task.setUpdatedAt(System.currentTimeMillis());
        taskDao.updateTaskStatus(task.getId(), isCompleted, task.getUpdatedAt());
        loadData();
    }

    @Override
    public void onTaskDelete(Task task) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    taskDao.delete(task);
                    loadData();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onFolderClick(Folder folder) {
        folderDao.updateCollapseState(folder.getId(), !folder.isCollapsed());
        folder.setCollapsed(!folder.isCollapsed());
        folderAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFolderAddTask(Folder folder) {
        showAddTaskDialog(folder.getId());
    }

    @Override
    public void onFolderDelete(Folder folder) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Folder")
                .setMessage("Are you sure you want to delete this folder and all its tasks?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    folderDao.delete(folder);
                    loadData();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onFolderTaskClick(Task task) { onTaskClick(task); }

    @Override
    public void onFolderTaskCheckChanged(Task task, boolean isCompleted) { onTaskCheckChanged(task, isCompleted); }

    @Override
    public void onFolderTaskDelete(Task task) { onTaskDelete(task); }
}