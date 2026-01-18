package com.example.todolist.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.todolist.models.Folder;

import java.util.List;

@Dao
public interface FolderDao {
    @Insert
    long insert(Folder folder);

    @Update
    void update(Folder folder);

    @Delete
    void delete(Folder folder);

    @Query("SELECT * FROM folders WHERE id = :id")
    Folder getFolderById(int id);

    @Query("SELECT * FROM folders WHERE userId = :userId ORDER BY name ASC")
    List<Folder> getFoldersByUserId(int userId);

    @Query("UPDATE folders SET isCollapsed = :isCollapsed WHERE id = :folderId")
    void updateCollapseState(int folderId, boolean isCollapsed);

    @Query("DELETE FROM folders WHERE id = :folderId")
    void deleteById(int folderId);
}

