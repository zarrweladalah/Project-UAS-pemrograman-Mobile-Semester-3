package com.example.todolist.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.models.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
        void onTaskCheckChanged(Task task, boolean isCompleted);
        void onTaskDelete(Task task);
    }

    public TaskAdapter(List<Task> tasks, OnTaskClickListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void updateTasks(List<Task> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private TextView titleText;
        private TextView descriptionText;
        private TextView dateText;
        private ImageButton deleteButton;
        private View priorityIndicator;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.task_checkbox);
            titleText = itemView.findViewById(R.id.task_title);
            descriptionText = itemView.findViewById(R.id.task_description);
            dateText = itemView.findViewById(R.id.task_date);
            deleteButton = itemView.findViewById(R.id.delete_button);
            priorityIndicator = itemView.findViewById(R.id.priority_indicator);
        }

        void bind(Task task) {
            titleText.setText(task.getTitle());
            checkBox.setChecked(task.isCompleted());

            // Strike through completed tasks
            if (task.isCompleted()) {
                titleText.setPaintFlags(titleText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                titleText.setAlpha(0.6f);
            } else {
                titleText.setPaintFlags(titleText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                titleText.setAlpha(1f);
            }

            // Description
            if (task.getDescription() != null && !task.getDescription().isEmpty()) {
                descriptionText.setVisibility(View.VISIBLE);
                descriptionText.setText(task.getDescription());
            } else {
                descriptionText.setVisibility(View.GONE);
            }

            // Date
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            dateText.setText(sdf.format(new Date(task.getCreatedAt())));

            // Priority indicator color
            switch (task.getPriority()) {
                case 1:
                    priorityIndicator.setBackgroundResource(R.color.priority_low);
                    break;
                case 2:
                    priorityIndicator.setBackgroundResource(R.color.priority_medium);
                    break;
                case 3:
                    priorityIndicator.setBackgroundResource(R.color.priority_high);
                    break;
            }

            // Click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClick(task);
                }
            });

            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(task.isCompleted());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onTaskCheckChanged(task, isChecked);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskDelete(task);
                }
            });
        }
    }
}

