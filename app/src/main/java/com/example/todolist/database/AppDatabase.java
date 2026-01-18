package com.example.todolist.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.todolist.models.Folder;
import com.example.todolist.models.Task;
import com.example.todolist.models.User;

@Database(entities = {User.class, Task.class, Folder.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;
    private static final String DATABASE_NAME = "todolist_database";

    public abstract UserDao userDao();
    public abstract TaskDao taskDao();
    public abstract FolderDao folderDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                    )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration() // Reset database jika schema berubah
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}

