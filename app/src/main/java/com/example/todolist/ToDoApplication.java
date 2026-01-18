package com.example.todolist;

import android.app.Application;

import com.example.todolist.database.AppDatabase;

public class ToDoApplication extends Application {
    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        database = AppDatabase.getInstance(this);
    }

    public AppDatabase getDatabase() {
        return database;
    }
}

