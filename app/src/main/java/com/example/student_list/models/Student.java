package com.example.student_list.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "big_table_of_students")
public class Student {

    @PrimaryKey(autoGenerate = true)
    long id;

    @ColumnInfo(name = "name_surname")
    private String name_surname;

    @ColumnInfo(name = "tel_number")
    private String tel_number;

    @ColumnInfo(name = "desc")
    private String desc;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "image", typeAffinity = ColumnInfo.BLOB )
    private byte[] image;

    public Student(String name_surname, String tel_number, String desc, String category, byte[] image) {
        this.name_surname = name_surname;
        this.tel_number = tel_number;
        this.desc = desc;
        this.category = category;
        this.image = image;
    }

    public String getDesc() {
        return desc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName_surname() {
        return name_surname;
    }

    public void setName_surname(String name_surname) {
        this.name_surname = name_surname;
    }

    public String getTel_number() {
        return tel_number;
    }

    public void setTel_number(String tel_number) {
        this.tel_number = tel_number;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
