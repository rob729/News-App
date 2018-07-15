package com.example.robin.news.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.robin.news.News;
import com.example.robin.news.NewsDao;

@Database(entities = {News.class}, version = 1)
public abstract class NewsDatabase extends RoomDatabase {
    public abstract NewsDao getNewsDao();
}
