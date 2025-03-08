package com.example.student_list.room;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.student_list.models.Student;

@Database(entities = {Student.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {
    public abstract StudentDao studentDao();
}
