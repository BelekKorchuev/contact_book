package com.example.student_list.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.student_list.models.Student;

import java.util.List;

@Dao
public interface StudentDao {
    @Query("SELECT * FROM big_table_of_students")
    List<Student> getAll();

    @Insert
    void insert(Student student);

    @Delete
    void delete(Student student);

    @Update
    void update(Student student);

    @Query("SELECT * FROM big_table_of_students ORDER BY name_surname ASC")
    List<Student> sortAll();

    @Query("SELECT * FROM big_table_of_students WHERE id = :id")
    Student getById(int id);
}
