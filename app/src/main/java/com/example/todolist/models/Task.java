package com.example.todolist.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo; // Import ini yang tadi hilang

@Entity(tableName = "tasks",
        indices = {
                @Index("userId"),
                @Index("folderId")
        },
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Folder.class,
                        parentColumns = "id",
                        childColumns = "folderId",
                        onDelete = ForeignKey.SET_NULL)
        })
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int userId;
    private Integer folderId;

    @NonNull
    private String title;

    private String description;
    private boolean isCompleted;

    @ColumnInfo(name = "priority") // Kita pasang ColumnInfo di sini saja
    private int priority; // Tetap pakai int: 1 = Low, 2 = Medium, 3 = High

    private long dueDate;
    private long createdAt;
    private long updatedAt;

    public Task(@NonNull String title, int userId) {
        this.title = title;
        this.userId = userId;
        this.isCompleted = false;
        this.priority = 2; // Default medium priority
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // --- GETTER & SETTER ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Integer getFolderId() { return folderId; }
    public void setFolderId(Integer folderId) { this.folderId = folderId; }

    @NonNull
    public String getTitle() { return title; }
    public void setTitle(@NonNull String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public long getDueDate() { return dueDate; }
    public void setDueDate(long dueDate) { this.dueDate = dueDate; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}