package com.example.todolist.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.todolist.models.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    long insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM tasks WHERE id = :id")
    Task getTaskById(int id);

    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY createdAt DESC")
    List<Task> getTasksByUserId(int userId);

    @Query("SELECT * FROM tasks WHERE userId = :userId AND folderId = :folderId ORDER BY createdAt DESC")
    List<Task> getTasksByFolder(int userId, int folderId);

    @Query("SELECT * FROM tasks WHERE userId = :userId AND folderId IS NULL ORDER BY createdAt DESC")
    List<Task> getTasksWithoutFolder(int userId);

    @Query("SELECT * FROM tasks WHERE userId = :userId AND isCompleted = 0 ORDER BY priority DESC, createdAt DESC")
    List<Task> getActiveTasks(int userId);

    @Query("SELECT * FROM tasks WHERE userId = :userId AND isCompleted = 1 ORDER BY updatedAt DESC")
    List<Task> getCompletedTasks(int userId);

    @Query("UPDATE tasks SET isCompleted = :isCompleted, updatedAt = :updatedAt WHERE id = :taskId")
    void updateTaskStatus(int taskId, boolean isCompleted, long updatedAt);

    @Query("DELETE FROM tasks WHERE id = :taskId")
    void deleteById(int taskId);

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId")
    int getTaskCount(int userId);

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND isCompleted = 1")
    int getCompletedTaskCount(int userId);
}

