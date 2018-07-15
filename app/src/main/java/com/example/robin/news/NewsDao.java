package com.example.robin.news;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface NewsDao {
    @Query("SELECT * FROM news_table")
    List<News> getAllNews();

    @Insert
    void insertNews(News... news);

    @Update
    void updateTask(News... news);

    @Delete
    void deleteTask(News... news);

}
