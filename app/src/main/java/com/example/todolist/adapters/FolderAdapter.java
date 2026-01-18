package com.example.todolist.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.database.TaskDao;
import com.example.todolist.models.Folder;
import com.example.todolist.models.Task;

import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {
    private List<Folder> folders;
    private OnFolderClickListener listener;
    private TaskDao taskDao;

    public interface OnFolderClickListener {
        void onFolderClick(Folder folder);
        void onFolderAddTask(Folder folder);
        void onFolderDelete(Folder folder);
        void onFolderTaskClick(Task task);
        void onFolderTaskCheckChanged(Task task, boolean isCompleted);
        void onFolderTaskDelete(Task task);
    }

    public FolderAdapter(List<Folder> folders, OnFolderClickListener listener, TaskDao taskDao) {
        this.folders = folders;
        this.listener = listener;
        this.taskDao = taskDao;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        Folder folder = folders.get(position);
        holder.bind(folder);
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    public void updateFolders(List<Folder> newFolders) {
        this.folders = newFolders;
        notifyDataSetChanged();
    }

    class FolderViewHolder extends RecyclerView.ViewHolder {
        private TextView folderName;
        private TextView taskCount;
        private ImageView expandIcon;
        private ImageButton addTaskButton;
        private ImageButton deleteButton;
        private LinearLayout folderHeader;
        private RecyclerView tasksRecyclerView;
        private LinearLayout tasksContainer;

        FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            folderName = itemView.findViewById(R.id.folder_name);
            taskCount = itemView.findViewById(R.id.folder_task_count);
            expandIcon = itemView.findViewById(R.id.expand_icon);
            addTaskButton = itemView.findViewById(R.id.folder_add_task);
            deleteButton = itemView.findViewById(R.id.folder_delete);
            folderHeader = itemView.findViewById(R.id.folder_header);
            tasksRecyclerView = itemView.findViewById(R.id.folder_tasks_recycler);
            tasksContainer = itemView.findViewById(R.id.tasks_container);
        }

        void bind(Folder folder) {
            folderName.setText(folder.getName());
            
            List<Task> folderTasks = taskDao.getTasksByFolder(folder.getUserId(), folder.getId());
            taskCount.setText(folderTasks.size() + " tasks");

            // Setup collapse/expand
            if (folder.isCollapsed()) {
                expandIcon.setRotation(0);
                tasksContainer.setVisibility(View.GONE);
            } else {
                expandIcon.setRotation(180);
                tasksContainer.setVisibility(View.VISIBLE);
            }

            // Setup nested tasks RecyclerView
            FolderTaskAdapter taskAdapter = new FolderTaskAdapter(folderTasks);
            tasksRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            tasksRecyclerView.setAdapter(taskAdapter);

            // Click listeners
            folderHeader.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFolderClick(folder);
                }
            });

            addTaskButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFolderAddTask(folder);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFolderDelete(folder);
                }
            });
        }

        // Inner adapter for folder tasks
        class FolderTaskAdapter extends RecyclerView.Adapter<FolderTaskAdapter.FolderTaskViewHolder> {
            private List<Task> tasks;

            FolderTaskAdapter(List<Task> tasks) {
                this.tasks = tasks;
            }

            @NonNull
            @Override
            public FolderTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_simple, parent, false);
                return new FolderTaskViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull FolderTaskViewHolder holder, int position) {
                Task task = tasks.get(position);
                holder.bind(task);
            }

            @Override
            public int getItemCount() {
                return tasks.size();
            }

            class FolderTaskViewHolder extends RecyclerView.ViewHolder {
                private CheckBox checkBox;
                private TextView titleText;
                private ImageButton deleteButton;

                FolderTaskViewHolder(@NonNull View itemView) {
                    super(itemView);
                    checkBox = itemView.findViewById(R.id.task_checkbox);
                    titleText = itemView.findViewById(R.id.task_title);
                    deleteButton = itemView.findViewById(R.id.delete_button);
                }

                void bind(Task task) {
                    titleText.setText(task.getTitle());
                    checkBox.setChecked(task.isCompleted());

                    if (task.isCompleted()) {
                        titleText.setPaintFlags(titleText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        titleText.setAlpha(0.6f);
                    } else {
                        titleText.setPaintFlags(titleText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        titleText.setAlpha(1f);
                    }

                    itemView.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onFolderTaskClick(task);
                        }
                    });

                    checkBox.setOnCheckedChangeListener(null);
                    checkBox.setChecked(task.isCompleted());
                    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (listener != null) {
                            listener.onFolderTaskCheckChanged(task, isChecked);
                        }
                    });

                    deleteButton.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onFolderTaskDelete(task);
                        }
                    });
                }
            }
        }
    }
}

